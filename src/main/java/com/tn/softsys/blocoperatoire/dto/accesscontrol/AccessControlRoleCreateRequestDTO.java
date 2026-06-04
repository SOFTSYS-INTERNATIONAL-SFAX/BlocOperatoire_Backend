package com.tn.softsys.blocoperatoire.dto.accesscontrol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

public record AccessControlRoleCreateRequestDTO(
        @NotBlank @Size(max = 100) String code,
        @Size(max = 255) String description,
        Set<String> permissionCodes
) {
    public Set<String> normalizedPermissionCodes() {
        return permissionCodes == null ? Set.of() : new LinkedHashSet<>(permissionCodes);
    }
}
