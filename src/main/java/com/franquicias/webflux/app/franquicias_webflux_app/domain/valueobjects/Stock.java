package com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.ExceptionReason;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.InvalidStockException;

public record Stock(Integer value) {
    public Stock {
        if (value == null ) {
            throw new InvalidStockException(ExceptionReason.NULL);
        }
        if (value < 0) {
            throw new InvalidStockException(ExceptionReason.NOT_POSITIVE);
        }
    }
    
    public Stock add(Integer amount) {
        return new Stock(this.value + amount);
    }
    
    public Stock subtract(Integer amount) {
        return new Stock(this.value - amount);
    }
}
