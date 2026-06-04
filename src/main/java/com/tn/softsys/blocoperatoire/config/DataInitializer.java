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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("=== DataInitializer started ===");

        Map<String, String> roleDefinitions = new LinkedHashMap<>();
        roleDefinitions.put("ADMIN", "Super Administrator");
        roleDefinitions.put("ADMINISTRATEUR_SYSTEME", "Administrateur systeme hospitalier");
        roleDefinitions.put("DIRECTION_MEDICALE", "Direction medicale");
        roleDefinitions.put("RESPONSABLE_QUALITE", "Responsable qualite");
        roleDefinitions.put("CADRE_BLOC", "Cadre de bloc");
        roleDefinitions.put("MEDECIN", "Medecin avec acces clinique transverse");
        roleDefinitions.put("CHIRURGIEN", "Medecin chirurgien");
        roleDefinitions.put("ANESTHESISTE", "Medecin anesthesiste");
        roleDefinitions.put("REANIMATEUR", "Medecin reanimateur");
        roleDefinitions.put("INFIRMIER", "Infirmier");
        roleDefinitions.put("IADE", "Infirmier anesthesiste diplome d'Etat");
        roleDefinitions.put("IBODE", "Infirmier de bloc operatoire diplome d'Etat");

        Map<String, Role> roles = new LinkedHashMap<>();
        roleDefinitions.forEach((code, description) -> roles.put(code, ensureRole(code, description)));

        Map<String, String> permissionDefinitions = new LinkedHashMap<>();
        permissionDefinitions.put("USER_MANAGE", "Administrer les utilisateurs et leurs habilitations");
        permissionDefinitions.put("USER_DIRECTORY_READ", "Consulter l'annuaire du personnel");
        permissionDefinitions.put("AUDIT_READ", "Consulter les journaux d'audit");
        permissionDefinitions.put("FHIR_RESOURCE_CREATE", "Creer une ressource FHIR");
        permissionDefinitions.put("FHIR_RESOURCE_READ", "Consulter les ressources FHIR");
        permissionDefinitions.put("PATIENT_READ", "Consulter les dossiers patients");
        permissionDefinitions.put("PATIENT_WRITE", "Modifier les dossiers patients");
        permissionDefinitions.put("PLANNING_READ", "Consulter le planning operatoire");
        permissionDefinitions.put("PLANNING_WRITE", "Modifier le planning operatoire");
        permissionDefinitions.put("OMS_VALIDATE", "Valider les etapes de la checklist OMS");
        permissionDefinitions.put("SCORE_VALIDATE", "Saisir et valider les scores cliniques");
        permissionDefinitions.put("MORGUE_ACCESS", "Acceder au module morgue");

        Map<String, Permission> permissions = new LinkedHashMap<>();
        permissionDefinitions.forEach((code, description) -> permissions.put(code, ensurePermission(code, description)));

        assignPermissions(roles.get("ADMIN"), permissions.values());
        assignPermissions(
                roles.get("ADMINISTRATEUR_SYSTEME"),
                Set.of(
                        permissions.get("USER_MANAGE"),
                        permissions.get("USER_DIRECTORY_READ"),
                        permissions.get("AUDIT_READ"),
                        permissions.get("FHIR_RESOURCE_READ"),
                        permissions.get("PATIENT_READ"),
                        permissions.get("PLANNING_READ")
                )
        );
        assignPermissions(
                roles.get("DIRECTION_MEDICALE"),
                Set.of(
                        permissions.get("USER_DIRECTORY_READ"),
                        permissions.get("AUDIT_READ"),
                        permissions.get("FHIR_RESOURCE_READ"),
                        permissions.get("PATIENT_READ"),
                        permissions.get("PLANNING_READ")
                )
        );
        assignPermissions(
                roles.get("RESPONSABLE_QUALITE"),
                Set.of(
                        permissions.get("USER_DIRECTORY_READ"),
                        permissions.get("AUDIT_READ"),
                        permissions.get("FHIR_RESOURCE_READ"),
                        permissions.get("PATIENT_READ"),
                        permissions.get("PLANNING_READ"),
                        permissions.get("OMS_VALIDATE")
                )
        );
        assignPermissions(
                roles.get("CADRE_BLOC"),
                Set.of(
                        permissions.get("USER_DIRECTORY_READ"),
                        permissions.get("PATIENT_READ"),
                        permissions.get("PLANNING_READ"),
                        permissions.get("PLANNING_WRITE"),
                        permissions.get("OMS_VALIDATE"),
                        permissions.get("FHIR_RESOURCE_READ")
                )
        );

        Set<Permission> clinicianPermissions = Set.of(
                permissions.get("USER_DIRECTORY_READ"),
                permissions.get("FHIR_RESOURCE_CREATE"),
                permissions.get("FHIR_RESOURCE_READ"),
                permissions.get("PATIENT_READ"),
                permissions.get("PATIENT_WRITE"),
                permissions.get("PLANNING_READ"),
                permissions.get("OMS_VALIDATE"),
                permissions.get("SCORE_VALIDATE")
        );
        assignPermissions(roles.get("MEDECIN"), clinicianPermissions);
        assignPermissions(roles.get("CHIRURGIEN"), clinicianPermissions);
        assignPermissions(roles.get("ANESTHESISTE"), clinicianPermissions);
        assignPermissions(roles.get("REANIMATEUR"), clinicianPermissions);

        Set<Permission> nursingPermissions = Set.of(
                permissions.get("USER_DIRECTORY_READ"),
                permissions.get("FHIR_RESOURCE_READ"),
                permissions.get("PATIENT_READ"),
                permissions.get("PLANNING_READ"),
                permissions.get("OMS_VALIDATE"),
                permissions.get("SCORE_VALIDATE"),
                permissions.get("MORGUE_ACCESS")
        );
        assignPermissions(roles.get("INFIRMIER"), nursingPermissions);
        assignPermissions(roles.get("IADE"), nursingPermissions);
        assignPermissions(
                roles.get("IBODE"),
                Set.of(
                        permissions.get("USER_DIRECTORY_READ"),
                        permissions.get("FHIR_RESOURCE_READ"),
                        permissions.get("PATIENT_READ"),
                        permissions.get("PLANNING_READ"),
                        permissions.get("OMS_VALIDATE"),
                        permissions.get("MORGUE_ACCESS")
                )
        );

        log.info("Roles and permissions initialized.");

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
                    .roles(Set.of(roles.get("ADMIN")))
                    .build();

            userRepository.save(admin);
            log.info("Admin user created successfully.");
        } else {
            log.info("Admin user already exists.");
        }

        log.info("=== DataInitializer completed ===");
    }

    private Role ensureRole(String code, String description) {
        return roleRepository.findByNom(code)
                .map(existing -> {
                    if (!description.equals(existing.getDescription())) {
                        existing.setDescription(description);
                        return roleRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    log.info("Creating role: {}", code);
                    return roleRepository.save(
                            Role.builder()
                                    .nom(code)
                                    .description(description)
                                    .build()
                    );
                });
    }

    private Permission ensurePermission(String code, String description) {
        return permissionRepository.findByCode(code)
                .map(existing -> {
                    if (!description.equals(existing.getDescription())) {
                        existing.setDescription(description);
                        return permissionRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder()
                                .code(code)
                                .description(description)
                                .build()
                ));
    }

    private void assignPermissions(Role role, Iterable<Permission> grantedPermissions) {
        Set<Permission> normalized = new LinkedHashSet<>();
        grantedPermissions.forEach(permission -> {
            if (permission != null) {
                normalized.add(permission);
            }
        });

        if (!normalized.equals(role.getPermissions())) {
            role.setPermissions(normalized);
            roleRepository.save(role);
        }
    }
}
