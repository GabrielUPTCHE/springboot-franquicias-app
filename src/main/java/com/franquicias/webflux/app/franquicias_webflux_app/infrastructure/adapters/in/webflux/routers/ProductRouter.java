package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.ProductHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Configuration
public class ProductRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/products",
            method = RequestMethod.POST,
            beanClass = ProductHandler.class,
            beanMethod = "createProduct",
            operation = @Operation(
                operationId = "createProduct",
                summary = "Crear un nuevo producto",
                description = "Agrega un nuevo producto y lo asocia a una sucursal específica."
            )
        ),
        @RouterOperation(
            path = "/api/v1/products/{id}",
            method = RequestMethod.DELETE,
            beanClass = ProductHandler.class,
            beanMethod = "deleteProduct",
            operation = @Operation(
                operationId = "deleteProduct",
                summary = "Eliminar un producto",
                description = "Elimina un producto del sistema utilizando su ID.",
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del producto a eliminar")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/products/{id}/stock",
            method = RequestMethod.PATCH,
            beanClass = ProductHandler.class,
            beanMethod = "updateProductStock",
            operation = @Operation(
                operationId = "updateProductStock",
                summary = "Actualizar el stock de un producto",
                description = "Modifica únicamente la cantidad de inventario (stock) de un producto existente.",
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del producto")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/products/{id}/name",
            method = RequestMethod.PATCH,
            beanClass = ProductHandler.class,
            beanMethod = "updateProductName",
            operation = @Operation(
                operationId = "updateProductName",
                summary = "Actualizar el nombre de un producto",
                description = "Modifica únicamente el nombre de un producto existente.",
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del producto")
                }
            )
        )
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/products", handler::createProduct)
                .DELETE("/api/v1/products/{id}", handler::deleteProduct)
                .PATCH("/api/v1/products/{id}/stock", handler::updateProductStock)
                .PATCH("/api/v1/products/{id}/name", handler::updateProductName)
                .build();
    }

}
