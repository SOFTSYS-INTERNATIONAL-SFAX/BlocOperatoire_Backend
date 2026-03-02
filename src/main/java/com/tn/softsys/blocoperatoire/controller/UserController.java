package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.user.UserCreateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.user.UserResponseDTO;
import com.tn.softsys.blocoperatoire.dto.user.UserUpdateRequestDTO;
import com.tn.softsys.blocoperatoire.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* =====================================================
       CREATE USER
       ===================================================== */

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO create(
            @Valid @RequestBody UserCreateRequestDTO dto
    ) {
        return userService.create(dto);
    }

    /* =====================================================
       GET USER BY ID
       ===================================================== */

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    /* =====================================================
       GET ALL USERS
       ===================================================== */

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAll() {
        return userService.getAll();
    }

    /* =====================================================
       UPDATE USER
       ===================================================== */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDTO dto
    ) {
        return userService.update(id, dto);
    }

    /* =====================================================
       SOFT DELETE (Disable account)
       ===================================================== */

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(@PathVariable UUID id) {
        userService.deactivate(id);
    }

    /* =====================================================
       HARD DELETE
       ===================================================== */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
