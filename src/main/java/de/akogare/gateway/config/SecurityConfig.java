package de.akogare.gateway.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  private final String[] freeResourceUrls = {
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/swagger-ui/**",
    "/v3/swagger-ui.html",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/api-docs/**",
    "/webjars/**",
    "/favicon.ico",
    "/aggregate/**",
    "/ws/**",
    "actuator/health"
  };

  @Value("${app.cors.origins}")
  private String corsOrigins;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrfSpec -> csrfSpec.disable())
        .authorizeExchange(
            exchange ->
                exchange
                    .pathMatchers(freeResourceUrls)
                    .permitAll()
                    .pathMatchers("*")
                    .permitAll()
                    .pathMatchers("/api/v1/echo-webservice/user")
                    .hasRole("USER")
                    .pathMatchers("/api/v1/echo-webservice/admin")
                    .hasRole("ADMIN")
                    .pathMatchers("/api/v1/user-webservice/**")
                    .hasRole("USER")
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2ResourceServerSpec ->
                oauth2ResourceServerSpec.jwt(
                    jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(getJwtAuthenticationConverter())));
    http.csrf(csrfSpec -> csrfSpec.disable());
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of("http://localhost:4200", "http://localhost:8080", "https://platform.akogare.de"));
    configuration.setAllowedMethods(List.of("GET", "PUT", "DELETE", "POST", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private Converter<Jwt, Mono<AbstractAuthenticationToken>> getJwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
  }
}
