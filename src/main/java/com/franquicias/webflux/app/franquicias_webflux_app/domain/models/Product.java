package com.franquicias.webflux.app.franquicias_webflux_app.domain.models;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;



public record Product(String id, String name, Stock stock,String branchId
){}