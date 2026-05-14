package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigValues {
    public static final Integer MAX_RETRIES = 3;
    public static final Integer RETRY_READ_SECONDS = 2;
    public static final Integer RETRY_WRITE_SECONDS = 4;

    
}
