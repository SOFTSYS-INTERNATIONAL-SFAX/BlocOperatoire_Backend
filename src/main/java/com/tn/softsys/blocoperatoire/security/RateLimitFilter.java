package com.tn.softsys.blocoperatoire.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 5;

    private static class AttemptData {
        int attempts;
        LocalDateTime lastAttempt;

        AttemptData(int attempts, LocalDateTime lastAttempt) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }
    }

    private final Map<String, AttemptData> attemptsCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if ("/api/auth/login".equals(path)
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String clientIp = request.getRemoteAddr();
            LocalDateTime now = LocalDateTime.now();

            AttemptData data = attemptsCache.get(clientIp);

            if (data != null) {

                long minutesSinceLast =
                        Duration.between(data.lastAttempt, now).toMinutes();

                if (minutesSinceLast >= BLOCK_DURATION_MINUTES) {
                    // reset après expiration
                    attemptsCache.remove(clientIp);
                } else if (data.attempts >= MAX_ATTEMPTS) {

                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");

                    response.getWriter().write("""
                        {
                          "error":"TOO_MANY_REQUESTS",
                          "message":"Too many login attempts. Try again later.",
                          "status":429
                        }
                        """);

                    return;
                }
            }

            // 🔥 On laisse passer l’auth
            filterChain.doFilter(request, response);

            // 🔥 Après traitement → si échec seulement
            if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {

                attemptsCache.compute(clientIp, (ip, existing) -> {
                    if (existing == null) {
                        return new AttemptData(1, now);
                    }
                    existing.attempts++;
                    existing.lastAttempt = now;
                    return existing;
                });

            } else if (response.getStatus() == HttpStatus.OK.value()) {

                // reset après succès
                attemptsCache.remove(clientIp);
            }

            return;
        }

        filterChain.doFilter(request, response);
    }
}