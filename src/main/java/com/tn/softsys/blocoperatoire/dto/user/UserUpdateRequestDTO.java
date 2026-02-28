package com.tn.softsys.blocoperatoire.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserUpdateRequestDTO {

    @Size(max = 100)
    private String nom;

    @Size(max = 100)
    private String prenom;

    @Email
    @Size(max = 100)
    private String email;

    @Size(min = 8)
    private String password;

    private Set<UUID> roleIds;

    private Boolean enabled;

    private Boolean accountNonLocked;

    private Boolean mfaEnabled;
}
