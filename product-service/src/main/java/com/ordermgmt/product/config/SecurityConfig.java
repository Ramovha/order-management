package com.ordermgmt.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for the Product Service.
 *
 * <p>This class configures Spring Security to protect all endpoints with HTTP Basic Authentication.
 * Public endpoints include:
 * <ul>
 *   <li>Health check (/health)</li>
 *   <li>Swagger UI (/swagger-ui/**)</li>
 *   <li>OpenAPI docs (/v3/api-docs/**)</li>
 *   <li>H2 Console (/h2-console/**) - for development purposes</li>
 * </ul>
 *
 * <p>All other endpoints require authentication with valid credentials.
 * Session management is configured as STATELESS to ensure compatibility with microservices.
 *
 * @author Order Management Team
 * @version 1.0.0
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * <p>Configuration includes:
     * <ul>
     *   <li>CSRF protection disabled for REST API</li>
     *   <li>HTTP Basic Authentication enabled</li>
     *   <li>Stateless session management</li>
     *   <li>Frame options allowing same-origin requests (for H2 Console)</li>
     * </ul>
     *
     * @param http The HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/health", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
