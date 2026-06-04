package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.user.CurrentAccessProfileDTO;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-control")
@RequiredArgsConstructor
public class AccessControlController {

    private final UserRepository userRepository;

    @GetMapping("/current-profile")
    public CurrentAccessProfileDTO currentProfile() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getNom())
                .toList();

        List<String> permissionCodes = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode())
                .distinct()
                .toList();

        return CurrentAccessProfileDTO.builder()
                .userId(user.getUserId().toString())
                .email(user.getEmail())
                .displayName((user.getPrenom() + " " + user.getNom()).trim())
                .roles(roles)
                .permissionCodes(permissionCodes)
                .build();
    }
}
