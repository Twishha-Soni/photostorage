package com.example.photostorage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain)
        throws ServletException, IOException {


        // 1.READ AUTHORIZATION HEADER
        String authHeader = request.getHeader("Authorization");

        // 2.IF NO TOKEN - SKIP AND LET SPRING SECURITY HANDLE IT
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3.EXTRACT TOKEN
        final String token = authHeader.substring(7);

        // 4.EXTRACT USERNAME
        String username = jwtService.extractUsername(token);

        // 5.IF USERNAME IS FOUND AND USER IS NOT YET AUTHENTICATED
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6.LOAD USER FROM DATABASE
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 7. VALIDATE TOKEN
            if(jwtService.isTokenValid(token)) {

                // 8.CREATE AUTHENTICATION OBJECT
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // 9.STORE IN SECURITY CONTEXT
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
