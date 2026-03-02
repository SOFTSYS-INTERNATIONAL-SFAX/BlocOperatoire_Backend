package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.user.*;
import com.tn.softsys.blocoperatoire.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /* ================= CREATE ================= */

    public User toEntity(UserCreateRequestDTO dto) {

        Set<Role> roles = dto.getRoleIds()
                .stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());

        return User.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(dto.getEnabled())
                .accountNonLocked(true)
                .failedAttempts(0)
                .mfaEnabled(false)
                .roles(roles)
                .build();
    }

    /* ================= UPDATE ================= */

    public void updateEntity(User user, UserUpdateRequestDTO dto) {

        if (dto.getNom() != null)
            user.setNom(dto.getNom());

        if (dto.getPrenom() != null)
            user.setPrenom(dto.getPrenom());

        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getEnabled() != null)
            user.setEnabled(dto.getEnabled());

        if (dto.getAccountNonLocked() != null)
            user.setAccountNonLocked(dto.getAccountNonLocked());

        if (dto.getMfaEnabled() != null)
            user.setMfaEnabled(dto.getMfaEnabled());

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {

            Set<Role> roles = dto.getRoleIds()
                    .stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found")))
                    .collect(Collectors.toSet());

            user.setRoles(roles);
        }
    }

    /* ================= ENTITY → DTO ================= */

    public UserResponseDTO toDTO(User user) {

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getNom)
                .collect(Collectors.toSet());

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .roles(roles)
                .accountNonLocked(user.getAccountNonLocked())
                .mfaEnabled(user.getMfaEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
