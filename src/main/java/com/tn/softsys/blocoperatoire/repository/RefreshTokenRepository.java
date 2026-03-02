package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser_UserId(UUID userId);
}
