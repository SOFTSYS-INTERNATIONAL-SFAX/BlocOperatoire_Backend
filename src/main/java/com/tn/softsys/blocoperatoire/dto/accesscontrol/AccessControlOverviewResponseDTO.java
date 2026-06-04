package com.tn.softsys.blocoperatoire.dto.accesscontrol;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AccessControlOverviewResponseDTO(
        List<RoleItem> roles,
        List<PermissionItem> permissions,
        List<String> modules,
        int totalUsers,
        int totalRoles,
        int totalPermissions,
        int customRoles
) {
    public record RoleItem(
            UUID roleId,
            String code,
            String label,
            String description,
            boolean systemRole,
            long userCount,
            Set<String> permissionCodes
    ) {}

    public record PermissionItem(
            UUID permissionId,
            String code,
            String module,
            String action,
            String label,
            String description
    ) {}
}
