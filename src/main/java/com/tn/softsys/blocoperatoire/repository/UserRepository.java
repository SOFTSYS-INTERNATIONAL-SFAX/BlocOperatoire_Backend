package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /* ==========================
       SECURITY
    ========================== */

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndUserIdNot(String email, UUID userId);
}
