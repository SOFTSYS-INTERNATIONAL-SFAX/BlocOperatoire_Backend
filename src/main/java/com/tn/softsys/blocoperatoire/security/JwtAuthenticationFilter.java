package com.tn.softsys.blocoperatoire.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        log.debug("Incoming request: {} {}", request.getMethod(), path);

        // 🔹 Ignore authentication endpoints
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 🔹 No header → let Spring handle 401
        if (authHeader == null) {
            log.debug("No Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            String email = jwtService.extractEmail(token);
            log.debug("Extracted email from token: {}", email);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                boolean valid = jwtService.isTokenValid(token, userDetails);
                log.debug("Token valid: {}", valid);

                if (valid) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    log.debug("JWT authentication success for user: {}", email);
                } else {
                    log.warn("Token validation failed for user: {}", email);
                }
            }

        } catch (ExpiredJwtException ex) {

            log.warn("JWT expired: {}", ex.getMessage());
            sendUnauthorized(response, "Token expired");
            return;

        } catch (JwtException ex) {

            log.warn("JWT invalid: {}", ex.getMessage());
            sendUnauthorized(response, "Invalid token");
            return;

        } catch (Exception ex) {

            log.error("Unexpected JWT error: {}", ex.getMessage());
            sendUnauthorized(response, "Authentication error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write("""
                {
                  "error": "UNAUTHORIZED",
                  "message": "%s",
                  "status": 401,
                  "timestamp": "%s"
                }
                """.formatted(message, LocalDateTime.now()));
    }
}