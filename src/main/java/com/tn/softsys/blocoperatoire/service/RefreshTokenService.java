package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.RefreshToken;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.RefreshTokenRepository;
import com.tn.softsys.blocoperatoire.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;

    @Value("${app.refresh-token.expiration}")
    private long refreshExpiration;

    /* =====================================================
       CREATE REFRESH TOKEN
       ===================================================== */

    public RefreshToken createRefreshToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();

        return repository.save(token);
    }

    /* =====================================================
       VERIFY EXPIRATION
       ===================================================== */

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        return token;
    }

    /* =====================================================
       FIND TOKEN
       ===================================================== */

    public RefreshToken findByToken(String token) {

        return repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));
    }

    /* =====================================================
       DELETE TOKEN (LOGOUT)
       ===================================================== */

    public void deleteByToken(String token) {

        repository.findByToken(token)
                .ifPresent(repository::delete);
    }
}
