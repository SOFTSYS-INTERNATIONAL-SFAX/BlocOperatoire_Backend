package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Permission;
import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.PermissionRepository;
import com.tn.softsys.blocoperatoire.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    /* ================================
       CREATE PERMISSION
    ================================= */

    public Permission create(Permission permission) {

        if (permissionRepository.existsByCode(permission.getCode())) {
            throw new IllegalStateException("Permission already exists");
        }

        return permissionRepository.save(permission);
    }

    /* ================================
       GET PERMISSIONS BY ROLE
    ================================= */

    public Set<Permission> findByRole(UUID roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found"));

        return role.getPermissions();
    }

    /* ================================
       GET BY ID
    ================================= */

    public Permission getById(UUID id) {

        return permissionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Permission not found"));
    }

    /* ================================
       DELETE
    ================================= */

    public void delete(UUID id) {

        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found");
        }

        permissionRepository.deleteById(id);
    }
}
