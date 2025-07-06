package com.cinebee.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import com.cinebee.config.JwtConfig;
import com.cinebee.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenService tokenService;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Bỏ qua JWT filter cho các đường dẫn trong White_List
        for (String pattern : com.cinebee.config.SecurityConfig.White_List) {
            if (pathMatcher.match(pattern, requestUri)) {
                System.out.println("Skipping JWT filter for whitelisted URI: " + requestUri);
                filterChain.doFilter(request, response);
                return;
            }
        }


        String token = getJwtFromRequest(request);
        System.out.println("Attempting to process JWT for URI: " + requestUri);
        System.out.println("Token found: " + (token != null ? "Yes" : "No"));

        if (token != null && jwtConfig.validateToken(token)) {
            System.out.println("Token is valid.");
            // Kiểm tra blacklist qua TokenService
            if (tokenService.isTokenBlacklisted(token)) {
                System.out.println("Token is blacklisted.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }
            String username = jwtConfig.getClaims(token).getSubject();
            System.out.println("Username from token: " + username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("User details loaded. Authorities: " + userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication set in SecurityContextHolder.");
        } else if (token != null) {
            System.out.println("Token is invalid.");
        } else {
            System.out.println("No token found.");
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // Ưu tiên lấy accessToken từ cookie HttpOnly
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // Fallback: lấy từ header Authorization nếu không có cookie
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}