package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.accesscontrol.AccessControlOverviewResponseDTO;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.AccessControlRoleCreateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.AccessControlRolePermissionUpdateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.accesscontrol.CurrentAccessProfileResponseDTO;
import com.tn.softsys.blocoperatoire.service.AccessControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/access-control")
@RequiredArgsConstructor
public class AccessControlController {

    private final AccessControlService accessControlService;

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public AccessControlOverviewResponseDTO getOverview() {
        return accessControlService.getOverview();
    }

    @GetMapping("/current-profile")
    @PreAuthorize("isAuthenticated()")
    public CurrentAccessProfileResponseDTO getCurrentProfile() {
        return accessControlService.getCurrentProfile();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public AccessControlOverviewResponseDTO.RoleItem createRole(@Valid @RequestBody AccessControlRoleCreateRequestDTO dto) {
        return accessControlService.createRole(dto);
    }

    @PutMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public AccessControlOverviewResponseDTO.RoleItem updateRolePermissions(
            @PathVariable UUID roleId,
            @Valid @RequestBody AccessControlRolePermissionUpdateRequestDTO dto
    ) {
        return accessControlService.updateRolePermissions(roleId, dto);
    }
}