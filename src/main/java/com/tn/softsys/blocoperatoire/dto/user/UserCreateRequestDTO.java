package com.tn.softsys.blocoperatoire.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserCreateRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Size(max = 100)
    private String prenom;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotEmpty(message = "Au moins un rôle est obligatoire")
    private Set<UUID> roleIds;

    private Boolean enabled = true;
}
