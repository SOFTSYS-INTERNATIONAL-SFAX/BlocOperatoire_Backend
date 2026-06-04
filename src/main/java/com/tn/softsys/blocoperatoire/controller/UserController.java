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

    @PostMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public UserResponseDTO create(@Valid @RequestBody UserCreateRequestDTO dto) {
        return userService.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER_MANAGE','USER_DIRECTORY_READ')")
    public UserResponseDTO getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER_MANAGE','USER_DIRECTORY_READ')")
    public List<UserResponseDTO> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public UserResponseDTO update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDTO dto) {
        return userService.update(id, dto);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public void deactivate(@PathVariable UUID id) {
        userService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
