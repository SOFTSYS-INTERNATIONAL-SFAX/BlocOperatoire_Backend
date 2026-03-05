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

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    // exemple: 1800000 = 30 minutes
    @Value("${jwt.expiration}")
    private long expiration;

    /* =======================================================
       SIGNING KEY
    ======================================================= */

    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /* =======================================================
       GENERATE ACCESS TOKEN
    ======================================================= */

    public String generateAccessToken(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getNom)
                .toList();

        return Jwts.builder()

                .setSubject(user.getEmail())

                .claim("roles", roles)
                .claim("nom", user.getNom())
                .claim("prenom", user.getPrenom())
                .claim("userId", user.getUserId())

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )

                .signWith(getSigningKey(), SignatureAlgorithm.HS256)

                .compact();
    }

    /* =======================================================
       EXTRACT EMAIL
    ======================================================= */

    public String extractEmail(String token) {

        return extractAllClaims(token).getSubject();
    }

    /* =======================================================
       EXTRACT EXPIRATION
    ======================================================= */

    public Date extractExpiration(String token) {

        return extractAllClaims(token).getExpiration();
    }

    /* =======================================================
       EXTRACT CLAIMS
    ======================================================= */

    public Claims extractAllClaims(String token) {

        try {

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (JwtException e) {

            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /* =======================================================
       TOKEN VALIDATION
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
       TOKEN EXPIRED ?
    ======================================================= */

    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /* =======================================================
       REMAINING TIME (useful for AFK)
    ======================================================= */

    public long getRemainingTime(String token) {

        Date expirationDate = extractExpiration(token);

        return expirationDate.getTime() - System.currentTimeMillis();
    }

}