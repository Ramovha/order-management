package com.ordermgmt.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration for the Order Service.
 *
 * <p>This configuration class provides a WebClient bean used for making reactive HTTP requests
 * to external services, particularly the Product Service. WebClient is the modern, non-blocking
 * alternative to RestTemplate.
 *
 * <p>The WebClient is used to:
 * <ul>
 *   <li>Validate product existence when creating orders</li>
 *   <li>Retrieve product details for price calculations</li>
 *   <li>Handle inter-microservice communication asynchronously</li>
 * </ul>
 *
 * @author Order Management Team
 * @version 1.0.0
 * @see org.springframework.web.reactive.function.client.WebClient
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a WebClient bean for reactive HTTP calls.
     *
     * <p>This WebClient can be injected into services that need to communicate with
     * external microservices such as the Product Service.
     *
     * @return A configured WebClient instance
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
