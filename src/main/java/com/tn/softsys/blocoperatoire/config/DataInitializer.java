package com.tn.softsys.blocoperatoire.config;

import com.tn.softsys.blocoperatoire.domain.Permission;
import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.PermissionRepository;
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
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {

        log.info("=== DataInitializer started ===");

        /* ========================
           0️⃣ PERMISSIONS
        ======================== */

        Permission pPatientRead   = perm("PATIENT_READ",         "Lire les patients");
        Permission pPatientWrite  = perm("PATIENT_WRITE",        "Modifier les patients");
        Permission pPlanningRead  = perm("PLANNING_READ",        "Lire le planning");
        Permission pOmsValidate   = perm("OMS_VALIDATE",         "Valider checklist OMS");
        Permission pScoreValidate = perm("SCORE_VALIDATE",       "Valider les scores médicaux");
        Permission pFhirCreate    = perm("FHIR_RESOURCE_CREATE", "Créer ressource FHIR");
        Permission pUserManage    = perm("USER_MANAGE",          "Gérer les utilisateurs");
        Permission pMorgueAccess  = perm("MORGUE_ACCESS",        "Accéder à la morgue");
        Permission pAuditRead     = perm("AUDIT_READ",           "Lire les audits");

        log.info("Permissions initialized.");

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

        // Assign permissions to roles (idempotent — reload to ensure Hibernate-managed collection)
        adminRole = roleRepository.findByNom("ADMIN").orElseThrow();
        chirurgienRole = roleRepository.findByNom("CHIRURGIEN").orElseThrow();
        anesthesisteRole = roleRepository.findByNom("ANESTHESISTE").orElseThrow();

        boolean adminUpdated = adminRole.getPermissions().addAll(Set.of(
                pPatientRead, pPatientWrite, pPlanningRead, pOmsValidate,
                pScoreValidate, pFhirCreate, pUserManage, pMorgueAccess, pAuditRead
        ));
        if (adminUpdated) roleRepository.save(adminRole);

        boolean chirurgienUpdated = chirurgienRole.getPermissions().addAll(Set.of(
                pPatientRead, pPatientWrite, pPlanningRead,
                pOmsValidate, pScoreValidate, pFhirCreate
        ));
        if (chirurgienUpdated) roleRepository.save(chirurgienRole);

        boolean anesthesisteUpdated = anesthesisteRole.getPermissions().addAll(Set.of(
                pPatientRead, pPlanningRead, pOmsValidate, pScoreValidate
        ));
        if (anesthesisteUpdated) roleRepository.save(anesthesisteRole);

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

    private Permission perm(String code, String description) {
        return permissionRepository.findByCode(code)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder()
                                .code(code)
                                .description(description)
                                .build()
                ));
    }
}
