package ru.otus.project.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/token", "/auth-service/v3/api-docs")
                        .uri("lb://auth-service"))

                .route("game-info-service", r -> r.path("/api/v1/game/**", "/game-info-service/v3/api-docs")
                        .uri("lb://game-info-service"))

                .route("achievement-service", r -> r.path("/api/v1/achievement/**",
                                "/api/v1/achievement-history/**", "/achievement-service/v3/api-docs")
                        .uri("lb://achievement-service"))

                .route("review-service", r -> r.path("/api/v1/review/**", "/api/v1/rating/**",
                                "/review-service/v3/api-docs")
                        .uri("lb://review-service"))

                .route("order-service", r -> r.path("/api/v1/order/**", "/api/v1/product/**",
                                "/order-service/v3/api-docs")
                        .uri("lb://order-service"))
                .build();
    }
}
