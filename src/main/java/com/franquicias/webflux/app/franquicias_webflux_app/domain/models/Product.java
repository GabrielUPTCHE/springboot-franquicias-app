package com.franquicias.webflux.app.franquicias_webflux_app.domain.models;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private Stock stock;
    private String branchId;
}