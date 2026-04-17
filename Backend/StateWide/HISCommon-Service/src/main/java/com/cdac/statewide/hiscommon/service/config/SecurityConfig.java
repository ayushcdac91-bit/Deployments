package com.cdac.statewide.hiscommon.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        /*
                         * .requestMatchers("/hiscommon/**").hasAnyRole("ADMIN", "USER")
                         * // Admin endpoints - only accessible by users with the "ROLE_ADMIN"
                         * .requestMatchers("/hiscommon/**").hasRole("ADMIN")
                         * // User endpoints - only accessible by users with the "ROLE_USER"
                         * .requestMatchers("/hiscommon/**").hasRole("USER") // Works with "ROLE_USER"
                         */

                        .requestMatchers("/his/common-service/**").hasAuthority("USER") // Works with "USER"

                        .anyRequest().authenticated() // All requests require authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add default
                                                                                                       // JWT filter

        return http.build();
    }
}
