package com.tn.softsys.blocoperatoire.security;

import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /* =======================================================
       SIGNING KEY
    ======================================================= */

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /* =======================================================
       ACCESS TOKEN
    ======================================================= */

    public String generateAccessToken(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getNom)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* =======================================================
       EXTRACTION
    ======================================================= */

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /* =======================================================
       VALIDATION
    ======================================================= */

    public boolean isTokenValid(String token, UserDetails userDetails) {

        try {
            final String email = extractEmail(token);

            return email.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /* =======================================================
       EXPIRATION CHECK
    ======================================================= */

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /* =======================================================
       PARSE CLAIMS
    ======================================================= */

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
