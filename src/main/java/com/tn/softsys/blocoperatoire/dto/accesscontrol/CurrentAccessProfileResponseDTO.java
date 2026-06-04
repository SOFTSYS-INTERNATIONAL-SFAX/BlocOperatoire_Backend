package com.tn.softsys.blocoperatoire.dto.accesscontrol;

import java.util.Set;
import java.util.UUID;

public record CurrentAccessProfileResponseDTO(
        UUID userId,
        String email,
        String displayName,
        Set<String> roles,
        Set<String> permissionCodes
) {
}