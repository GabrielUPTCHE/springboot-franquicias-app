package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.FranchiseHandler;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/franchises", handler::createFranchise)
                .build();
    }
}