package com.tn.softsys.blocoperatoire.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if ("/api/auth/login".equals(path)
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String clientIp = request.getRemoteAddr();

            int attempts = attemptsCache.getOrDefault(clientIp, 0);

            if (attempts >= MAX_ATTEMPTS) {

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");

                response.getWriter().write("""
                    {
                      "error":"TOO_MANY_REQUESTS",
                      "message":"Too many login attempts",
                      "status":429,
                      "timestamp":"%s"
                    }
                    """.formatted(LocalDateTime.now()));

                return; // 🔴 BLOQUE ICI
            }

            // 🔥 INCRÉMENTATION AVANT AUTH
            attemptsCache.put(clientIp, attempts + 1);
        }

        filterChain.doFilter(request, response);
    }
}
