package com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions;

public class InvalidStockException extends DomainException{

    public InvalidStockException(ExceptionReason reason) {
        super(buildMessage(reason));
    }

    private static String buildMessage(ExceptionReason reason) {
        switch (reason) {
            case NULL:
                return "El stock es obligatorio";
            case NOT_POSITIVE:
                return "El stock debe ser un valor positivo";
            default:
                return "Error en el stock";
        }
    }
    
}
