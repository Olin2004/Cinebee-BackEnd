package com.cinebee.config;

import static org.springframework.security.config.Customizer.*;

import java.util.List;

import com.cinebee.common.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.cinebee.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize annotations
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public static final  String[] White_List= {
            "/api/auth/**",
            "/api/movies/trending", "/api/movies","/api/movies/all-by-likes","/api/movies/search",
            "/api/banner/active",
            "/api/v1/payments/momo/ipn",
            "/api/v1/payments/momo/return",
            "/api/movies/clear-cache",
            "/api/v1/tickets/showtimes/*/seats", // Cho phép xem ghế available
            "/v3/api-docs/**"

    };
    public static final String[] Admin_Only_List = {
            "/api/movies/add-new-film",
            "/api/movies/update-film",
            "/api/movies/delete-film",
            "/api/banner/add-banner",
            "/api/movies/list-movies"

    };
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Cấu hình mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình filter chain bảo mật HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // <-- cần có để CorsFilter được áp dụng
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(White_List).permitAll()
                        .requestMatchers(Admin_Only_List).hasRole(Role.ADMIN.name())
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/payments/momo/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/test/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // CORS Filter đúng chuẩn Spring Security 6.1+ (thay cho .cors() đã deprecated)
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080")); // FE domain
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // Cấu hình Global AuthenticationManager (không dùng userDetailsService ở đây)
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Không cấu hình vì dùng JWT
    }

    // Expose AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
