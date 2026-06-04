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

    private static final int MAX_ATTEMPTS = 20;
    private static final long WINDOW_MS = 15 * 60 * 1000L; // 15 minutes

    // long[0] = attempt count, long[1] = window start epoch ms
    private final Map<String, long[]> attemptsCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if ("/api/auth/login".equals(path)
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String clientIp = request.getRemoteAddr();
            long now = System.currentTimeMillis();

            long[] entry = attemptsCache.compute(clientIp, (ip, existing) -> {
                if (existing == null || now - existing[1] >= WINDOW_MS) {
                    return new long[]{1, now};
                }
                existing[0]++;
                return existing;
            });

            if (entry[0] > MAX_ATTEMPTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                    {
                      "error":"TOO_MANY_REQUESTS",
                      "message":"Too many login attempts. Try again in 15 minutes.",
                      "status":429,
                      "timestamp":"%s"
                    }
                    """.formatted(LocalDateTime.now()));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
