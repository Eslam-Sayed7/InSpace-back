package com.InSpace.Api.config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**", "/api/user/**")
                        .permitAll() // Public endpoints

                        // .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(),
                        // MANAGER.name())
                        // .requestMatchers(GET,
                        // "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(),
                        // MANAGER_READ.name())
                        // .requestMatchers(POST,
                        // "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(),
                        // MANAGER_CREATE.name())
                        // .requestMatchers(PUT,
                        // "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(),
                        // MANAGER_UPDATE.name())
                        // .requestMatchers(DELETE,
                        // "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(),
                        // MANAGER_DELETE.name())

                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint)) // Handle unauthorized access
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // JWT Filter
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }
}
