package com.crm.crm_lite.security;

import com.crm.crm_lite.config.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtFilter  jwtFilter;

    public SecurityConfig(CorsConfig corsConfig, JwtFilter jwtFilter) {
        this.corsConfig = corsConfig;
        this.jwtFilter  = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/leads", "/api/leads/**",
                                "/api/notes/lead/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "MANAGER", "SALES_REPRESENTATIVE")
                        .requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "MANAGER", "SALES_REPRESENTATIVE")
                        .requestMatchers("/api/contacts/**").hasAnyRole("ADMIN", "MANAGER", "SALES_REPRESENTATIVE")
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "MANAGER", "SALES_REPRESENTATIVE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}