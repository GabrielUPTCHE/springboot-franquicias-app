package com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects;

import java.util.UUID;

public record BranchId(String value) {

    public BranchId{
        if (value == null) {
            throw new RuntimeException("error");
        }
    }

    
    public static BranchId newId(){
        return new BranchId(UUID.randomUUID().toString());
    }

}
