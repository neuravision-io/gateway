package de.akogare.gateway.routes;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Configuration
public class Routes {
  private static final Logger logger = LoggerFactory.getLogger(Routes.class);

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  String issuerUri;

  @Bean
  public JwtDecoder customJwtDecoder() {
    NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);

    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();

    OAuth2TokenValidator<Jwt> validator =
        new DelegatingOAuth2TokenValidator<>(withIssuer, withTimestamp);
    jwtDecoder.setJwtValidator(validator);

    return jwtDecoder;
  }

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtDecoder jwtDecoder) {
    return builder
        .routes()
        .route(
            "echo-webservice",
            r ->
                r.path("/api/v1/echo-webservice/**")
                    .filters(
                        f ->
                            f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .filter(
                                    (exchange, chain) -> {
                                      ServerHttpRequest request = exchange.getRequest();
                                      String token =
                                          request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                                      if (token != null && token.startsWith("Bearer ")) {
                                        String jwtToken = token.substring(7);
                                        try {
                                          Jwt jwt = jwtDecoder.decode(jwtToken);
                                          String userId = jwt.getSubject();
                                          ServerHttpRequest modifiedRequest =
                                              request.mutate().header("X-User-ID", userId).build();
                                          return chain.filter(
                                              exchange.mutate().request(modifiedRequest).build());
                                        } catch (Exception e) {
                                          // Handle JWT decoding error
                                          return Mono.error(
                                              new ResponseStatusException(
                                                  HttpStatus.UNAUTHORIZED, "Invalid token"));
                                        }
                                      }
                                      return chain.filter(exchange);
                                    }))
                    .uri("lb://echo-webservice"))
        .route(
            "user-webservice",
            r ->
                r.path("/api/v1/user-webservice/**")
                    .filters(
                        f ->
                            f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .filter(
                                    (exchange, chain) -> {
                                      return chain.filter(exchange);
                                    }))
                    .uri("lb://user-webservice"))
        .route(
            "document-webservice",
            r ->
                r.path("/api/v1/document-webservice/**")
                    .filters(
                        f ->
                            f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .filter(
                                    (exchange, chain) -> {
                                      ServerHttpRequest request = exchange.getRequest();
                                      String token =
                                          request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                                      if (token != null && token.startsWith("Bearer ")) {
                                        String jwtToken = token.substring(7);
                                        try {
                                          Jwt jwt = jwtDecoder.decode(jwtToken);
                                          String userId = jwt.getSubject();
                                          ServerHttpRequest modifiedRequest =
                                              request.mutate().header("X-User-ID", userId).build();
                                          return chain.filter(
                                              exchange.mutate().request(modifiedRequest).build());
                                        } catch (Exception e) {
                                          // Handle JWT decoding error
                                          return Mono.error(
                                              new ResponseStatusException(
                                                  HttpStatus.UNAUTHORIZED, "Invalid token"));
                                        }
                                      }
                                      return chain.filter(exchange);
                                    }))
                    .uri("lb://document-webservice"))
        .route(
            "pdf_service_swagger",
            r ->
                r.path("/aggregate/echo-webservice/v3/api-docs")
                    .filters(
                        f -> f.rewritePath("/aggregate/echo-webservice/v3/api-docs", "/api-docs"))
                    .uri("lb://echo-webservice"))
        .route(
            "user_service_swagger",
            r ->
                r.path("/aggregate/user-webservice/v3/api-docs")
                    .filters(
                        f -> f.rewritePath("/aggregate/user-webservice/v3/api-docs", "/api-docs"))
                    .uri("lb://user-webservice"))
        .route(
            "upload_service_swagger",
            r ->
                r.path("/aggregate/document-webservice/v3/api-docs")
                    .filters(
                        f ->
                            f.rewritePath(
                                "/aggregate/document-webservice/v3/api-docs", "/api-docs"))
                    .uri("lb://document-webservice"))
        .route("websocket-webservice", r -> r.path("/ws/**").uri("lb://websocket-webservice"))
        .build();
  }
}
