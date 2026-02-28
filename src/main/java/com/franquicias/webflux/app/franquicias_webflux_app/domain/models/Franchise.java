package com.franquicias.webflux.app.franquicias_webflux_app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Franchise {
    private String id;
    private String name;
}