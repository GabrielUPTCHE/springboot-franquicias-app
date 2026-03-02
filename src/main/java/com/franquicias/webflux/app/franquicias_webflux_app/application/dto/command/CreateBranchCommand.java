package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;


public record CreateBranchCommand(
    String name,
    String franchiseId
) {}