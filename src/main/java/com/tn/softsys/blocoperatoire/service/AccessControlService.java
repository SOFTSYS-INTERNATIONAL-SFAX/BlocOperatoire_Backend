package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Permission;
import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.AccessControlOverviewResponseDTO;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.AccessControlRoleCreateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.AccessControlRolePermissionUpdateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.CurrentAccessProfileResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.PermissionRepository;
import com.tn.softsys.blocoperatoire.repository.RoleRepository;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccessControlService {

    private static final String MODULE = "ACCESS_CONTROL";
    private static final Set<String> SYSTEM_ROLE_CODES = Set.of(
            "ADMIN",
            "ADMINISTRATEUR_SYSTEME",
            "DIRECTION_MEDICALE",
            "RESPONSABLE_QUALITE",
            "CADRE_BLOC",
            "MEDECIN",
            "CHIRURGIEN",
            "ANESTHESISTE",
            "REANIMATEUR",
            "INFIRMIER",
            "IADE",
            "IBODE"
    );

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Transactional(readOnly = true)
    public AccessControlOverviewResponseDTO getOverview() {
        List<Role> roles = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
        List<Permission> permissions = permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));

        List<AccessControlOverviewResponseDTO.PermissionItem> permissionItems = permissions.stream()
                .map(this::toPermissionItem)
                .sorted(Comparator.comparing(AccessControlOverviewResponseDTO.PermissionItem::module)
                        .thenComparing(AccessControlOverviewResponseDTO.PermissionItem::action)
                        .thenComparing(AccessControlOverviewResponseDTO.PermissionItem::label))
                .toList();

        List<AccessControlOverviewResponseDTO.RoleItem> roleItems = roles.stream()
                .map(this::toRoleItem)
                .toList();

        audit("ACCESS_CONTROL_OVERVIEW_READ", null,
                "Consultation habilitations roles=" + roleItems.size() + " permissions=" + permissionItems.size());

        return new AccessControlOverviewResponseDTO(
                roleItems,
                permissionItems,
                permissionItems.stream().map(AccessControlOverviewResponseDTO.PermissionItem::module).distinct().toList(),
                (int) userRepository.count(),
                roleItems.size(),
                permissionItems.size(),
                (int) roleItems.stream().filter(role -> !role.systemRole()).count()
        );
    }

    @Transactional(readOnly = true)
    public CurrentAccessProfileResponseDTO getCurrentProfile() {
        User user = auditContextService.getCurrentUserOrNull();
        if (user == null) {
            throw new ResourceNotFoundException("Current user not found");
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getNom)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> permissionCodes = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        audit("ACCESS_CONTROL_PROFILE_READ", user.getUserId(),
                "Chargement profil habilitations email=" + user.getEmail() + " roles=" + roles + " permissions=" + permissionCodes.size());

        return new CurrentAccessProfileResponseDTO(
                user.getUserId(),
                user.getEmail(),
                (user.getPrenom() + " " + user.getNom()).trim(),
                roles,
                permissionCodes
        );
    }

    public AccessControlOverviewResponseDTO.RoleItem createRole(AccessControlRoleCreateRequestDTO dto) {
        String code = normalizeCode(dto.code());
        if (roleRepository.existsByNom(code)) {
            throw new IllegalArgumentException("Ce role existe deja: " + code);
        }

        Role role = Role.builder()
                .nom(code)
                .description(normalizeDescription(dto.description(), code))
                .permissions(resolvePermissions(dto.normalizedPermissionCodes()))
                .build();

        Role saved = roleRepository.save(role);
        audit("ACCESS_CONTROL_ROLE_CREATE", saved.getRoleId(),
                "Creation role code=" + saved.getNom() + " permissions=" + saved.getPermissions().stream().map(Permission::getCode).sorted().toList());
        return toRoleItem(saved);
    }

    public AccessControlOverviewResponseDTO.RoleItem updateRolePermissions(UUID roleId, AccessControlRolePermissionUpdateRequestDTO dto) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));

        Set<String> before = role.getPermissions().stream().map(Permission::getCode).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Permission> nextPermissions = resolvePermissions(dto.normalizedPermissionCodes());
        role.setPermissions(nextPermissions);
        Role saved = roleRepository.save(role);

        Set<String> after = saved.getPermissions().stream().map(Permission::getCode).collect(Collectors.toCollection(LinkedHashSet::new));
        audit("ACCESS_CONTROL_ROLE_PERMISSIONS_UPDATE", saved.getRoleId(),
                "Mise a jour role=" + saved.getNom() + " avant=" + before + " apres=" + after);
        return toRoleItem(saved);
    }

    private AccessControlOverviewResponseDTO.RoleItem toRoleItem(Role role) {
        return new AccessControlOverviewResponseDTO.RoleItem(
                role.getRoleId(),
                role.getNom(),
                buildRoleLabel(role.getNom()),
                role.getDescription(),
                SYSTEM_ROLE_CODES.contains(role.getNom()),
                role.getUsers() == null ? 0 : role.getUsers().size(),
                role.getPermissions().stream()
                        .map(Permission::getCode)
                        .sorted()
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    private AccessControlOverviewResponseDTO.PermissionItem toPermissionItem(Permission permission) {
        PermissionMeta meta = toPermissionMeta(permission.getCode());
        return new AccessControlOverviewResponseDTO.PermissionItem(
                permission.getPermissionId(),
                permission.getCode(),
                meta.module(),
                meta.action(),
                meta.label(),
                permission.getDescription()
        );
    }

    private Set<Permission> resolvePermissions(Set<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return new LinkedHashSet<>();
        }

        return permissionCodes.stream()
                .map(this::normalizeCode)
                .map(code -> permissionRepository.findByCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + code)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeCode(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDescription(String description, String code) {
        String normalized = description == null ? "" : description.trim();
        return normalized.isBlank() ? "Role hospitalier " + buildRoleLabel(code) : normalized;
    }

    private String buildRoleLabel(String code) {
        return switch (code) {
            case "ADMIN" -> "Administrateur";
            case "ADMINISTRATEUR_SYSTEME" -> "Administrateur systeme";
            case "DIRECTION_MEDICALE" -> "Direction medicale";
            case "RESPONSABLE_QUALITE" -> "Responsable qualite";
            case "CADRE_BLOC" -> "Cadre de bloc";
            case "MEDECIN" -> "Medecin";
            case "CHIRURGIEN" -> "Chirurgien";
            case "ANESTHESISTE" -> "Anesthesiste";
            case "REANIMATEUR" -> "Reanimateur";
            case "INFIRMIER" -> "Infirmier";
            case "IADE" -> "IADE";
            case "IBODE" -> "IBODE";
            default -> code.replace('_', ' ');
        };
    }

    private PermissionMeta toPermissionMeta(String code) {
        return switch (code) {
            case "USER_MANAGE" -> new PermissionMeta("UTILISATEURS", "MANAGE", "Gestion utilisateurs");
            case "AUDIT_READ" -> new PermissionMeta("AUDIT", "READ", "Lecture audit");
            case "FHIR_RESOURCE_CREATE" -> new PermissionMeta("FHIR", "CREATE", "Creation ressource FHIR");
            case "FHIR_RESOURCE_READ" -> new PermissionMeta("FHIR", "READ", "Lecture ressource FHIR");
            case "PATIENT_READ" -> new PermissionMeta("PATIENTS", "READ", "Lecture dossier patient");
            case "PATIENT_WRITE" -> new PermissionMeta("PATIENTS", "WRITE", "Modification dossier patient");
            case "PLANNING_READ" -> new PermissionMeta("PLANNING", "READ", "Lecture planning");
            case "PLANNING_WRITE" -> new PermissionMeta("PLANNING", "WRITE", "Modification planning");
            case "OMS_VALIDATE" -> new PermissionMeta("OMS", "VALIDATE", "Validation checklist OMS");
            case "SCORE_VALIDATE" -> new PermissionMeta("SCORES", "VALIDATE", "Validation scores cliniques");
            case "MORGUE_ACCESS" -> new PermissionMeta("MORGUE", "ACCESS", "Acces morgue");
            default -> {
                String[] parts = code.split("_");
                String action = parts.length > 1 ? parts[parts.length - 1] : "ACCESS";
                String module = parts.length > 1 ? String.join("_", java.util.Arrays.copyOf(parts, parts.length - 1)) : code;
                yield new PermissionMeta(module, action, code.replace('_', ' '));
            }
        };
    }

    private void audit(String action, UUID referenceId, String details) {
        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                action,
                MODULE,
                referenceId,
                details,
                auditContextService.getClientIp()
        );
    }

    private record PermissionMeta(String module, String action, String label) {}
}