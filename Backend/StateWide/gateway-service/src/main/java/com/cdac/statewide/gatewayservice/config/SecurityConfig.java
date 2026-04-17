package com.cdac.statewide.gatewayservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.cdac.statewide.gatewayservice.filters.JwtAuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Autowired
        private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .cors(Customizer.withDefaults())
                                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                                .authorizeExchange(exchange -> exchange
                                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .pathMatchers(
                                                                "/auth/login",
                                                                "/auth/forgot/**",
                                                                "/auth/refresh",
                                                                "/auth/register"

                                                )
                                                .permitAll()
                                                .anyExchange().authenticated()
                                // .anyExchange().permitAll()
                                )
                                .build();
        }
}