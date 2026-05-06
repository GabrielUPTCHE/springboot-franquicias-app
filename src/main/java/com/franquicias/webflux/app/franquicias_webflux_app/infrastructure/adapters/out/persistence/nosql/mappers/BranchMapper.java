package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers;

import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.BranchDocument;

@Component
public class BranchMapper {


    public static Branch toDomain(BranchDocument branchDocument) {
        return new Branch(branchDocument.getId(), branchDocument.getName(), branchDocument.getFranchiseId());
    }

    public static BranchDocument toEntity(Branch branch) {
        return BranchDocument.builder()
                .id(branch.id())
                .name(branch.name())
                .franchiseId(branch.franchiseId())
                .build();
    }
}