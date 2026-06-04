package com.tn.softsys.blocoperatoire.dto.accesscontrol;

import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public record AccessControlRolePermissionUpdateRequestDTO(
        @NotNull Set<String> permissionCodes
) {
    public Set<String> normalizedPermissionCodes() {
        return permissionCodes == null ? Set.of() : new LinkedHashSet<>(permissionCodes);
    }
}
