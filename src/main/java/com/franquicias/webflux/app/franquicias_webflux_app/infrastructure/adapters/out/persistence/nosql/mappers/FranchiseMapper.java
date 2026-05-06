package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers;

import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.FranchiseDocument;

@Component
public class FranchiseMapper {

    public static Franchise toDomain(FranchiseDocument franchiseDocument) {
        return new Franchise(franchiseDocument.getId(), franchiseDocument.getName());
    }

    public static FranchiseDocument toEntity(Franchise franchise) {
        return FranchiseDocument.builder()
                .id(franchise.id())
                .name(franchise.name())
                .build();
    }
}