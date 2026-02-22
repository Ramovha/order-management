package com.ordermgmt.order.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for WebClient and RestTemplate beans.
 * Provides HTTP client for inter-service communication.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Configuration
public class WebClientConfig {

    /**
     * Create RestTemplate bean for synchronous HTTP calls.
     * Used for calling Product Service.
     * 
     * @param builder RestTemplateBuilder
     * @return Configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(java.time.Duration.ofSeconds(5))
                .setReadTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }
}
