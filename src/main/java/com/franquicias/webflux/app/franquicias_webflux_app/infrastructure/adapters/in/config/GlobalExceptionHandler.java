package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.BranchNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.InvalidStockException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.ProductNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.dto.ErrorResponse;

import reactor.core.publisher.Mono;

@Component
@Order(-2) 
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(
            ErrorAttributes errorAttributes,
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer) {
        
        super(errorAttributes, new WebProperties().getResources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        
        HttpStatus status;
        String errorMessage = error.getMessage();

        if (error instanceof ProductNotFoundException || 
            error instanceof BranchNotFoundException || 
            error instanceof FranchiseNotFoundException) {
            
            status = HttpStatus.NOT_FOUND; // 404
            
        } else if (error instanceof InvalidStockException || error instanceof IllegalArgumentException) {
            
            status = HttpStatus.BAD_REQUEST; // 400
            
        } else if (error instanceof ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            errorMessage = responseStatusException.getReason();
            
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            errorMessage = "Ocurrió un error interno en el servidor.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                errorMessage,
                request.path()
        );

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}