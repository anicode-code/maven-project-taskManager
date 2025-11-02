package com.example.taskapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Security configuration with CORS support.
 *
 * Replace your existing SecurityConfig.java with this file (or adapt accordingly).
 * This enables CORS for the security filter chain and provides a CorsConfigurationSource
 * bean that allows your frontend origin(s). Adjust allowedOrigins for production.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Create JwtAuthFilter bean if you have such a class that takes JwtUtil
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())          // enable CORS support
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // allow preflight OPTIONS requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // allow unauthenticated access to auth endpoints and H2 console
                .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            // if using H2 console in dev
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /**
     * Configure CORS for the application.
     *
     * IMPORTANT:
     * - For development list specific origins (e.g., http://localhost:53717)
     * - For production replace with your real domain(s)
     * - If allowCredentials is true, Access-Control-Allow-Origin must not be "*"
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins - add your frontend origins here (exact match)
        config.setAllowedOrigins(List.of(
            "http://localhost:8080",    // allow same-origin if needed
            "http://127.0.0.1:8080",
            "http://localhost:5173",
            "http://127.0.0.1:5173"
            // add production domain(s) here, e.g. "https://yourdomain.com"
        ));

        // Allowed HTTP methods for CORS
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers the client might send, including Authorization
        config.setAllowedHeaders(List.of("*"));

        // Expose specific headers to the client if needed
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        // Allow sending cookies or Authorization headers
        config.setAllowCredentials(true);

        // Optional: preflight cache duration (seconds)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // apply this CORS config to all paths
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
