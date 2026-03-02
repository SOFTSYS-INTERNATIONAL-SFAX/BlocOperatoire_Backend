package com.tn.softsys.blocoperatoire.config;

import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.RoleRepository;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        log.info("=== DataInitializer started ===");

        /* ========================
           1️⃣ CREATION DES ROLES
        ======================== */

        Role adminRole = roleRepository.findByNom("ADMIN")
                .orElseGet(() -> {
                    log.info("Creating role: ADMIN");
                    return roleRepository.save(
                            Role.builder()
                                    .nom("ADMIN")
                                    .description("Super Administrator")
                                    .build()
                    );
                });

        Role chirurgienRole = roleRepository.findByNom("CHIRURGIEN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .nom("CHIRURGIEN")
                                .description("Médecin Chirurgien")
                                .build()
                ));

        Role anesthesisteRole = roleRepository.findByNom("ANESTHESISTE")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .nom("ANESTHESISTE")
                                .description("Médecin Anesthésiste")
                                .build()
                ));

        log.info("Roles initialized.");

        /* ========================
           2️⃣ CREATION ADMIN USER
        ======================== */

        if (!userRepository.existsByEmail("admin@bloc.com")) {

            log.info("Creating admin user...");

            User admin = User.builder()
                    .nom("Admin")
                    .prenom("System")
                    .email("admin@bloc.com")
                    .password(passwordEncoder.encode("123456"))
                    .enabled(true)
                    .accountNonLocked(true)
                    .failedAttempts(0)
                    .mfaEnabled(false)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);

            log.info("Admin user created successfully.");

        } else {
            log.info("Admin user already exists.");
        }

        log.info("=== DataInitializer completed ===");
    }
}
