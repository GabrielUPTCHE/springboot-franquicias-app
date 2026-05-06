package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.BranchDocument;

public final class BranchMapper {

    private BranchMapper() {}

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