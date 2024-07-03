package de.akogare.gateway.routes;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@Configuration
public class Routes {
    private static final Logger logger = LoggerFactory.getLogger(Routes.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("echo-webservice", r -> r.path("/api/echo-webservice/**")
                        .filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .filter((exchange, chain) -> {
                                    return chain.filter(exchange);
                                })
                        )
                        .uri("lb://echo-webservice"))
                .route("pdf_service_swagger", r -> r.path("/aggregate/echo-webservice/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/echo-webservice/v3/api-docs", "/api-docs"))
                        .uri("lb://echo-webservice"))
                /*websocket route*/
/*            .route("pdf_analyzer_service", r -> r.path("/ws/**")
                    .uri("lb://pdf-analyzer-webservice"))*/
                .build();
    }


}
