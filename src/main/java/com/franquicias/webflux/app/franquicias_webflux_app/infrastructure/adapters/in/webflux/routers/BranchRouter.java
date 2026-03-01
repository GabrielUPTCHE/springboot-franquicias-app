package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.BranchHandler;

@Configuration
public class BranchRouter {

    @Bean
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/branches", handler::createBranch)
                .PATCH("/api/v1/branches/{id}/name", handler::updateBranchName)
                .build();
    }
}