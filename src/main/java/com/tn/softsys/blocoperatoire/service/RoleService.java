package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role create(Role role) {
        if (roleRepository.existsByNom(role.getNom())) {
            throw new IllegalStateException("Role already exists");
        }
        return roleRepository.save(role);
    }

    public Role getById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public void delete(UUID id) {
        roleRepository.deleteById(id);
    }
}
