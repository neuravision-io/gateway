package de.akogare.gateway.routes;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {
  private static final Logger logger = LoggerFactory.getLogger(Routes.class);

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
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
