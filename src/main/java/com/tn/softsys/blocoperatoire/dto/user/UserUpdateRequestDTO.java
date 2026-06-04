package com.tn.softsys.blocoperatoire.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateRequestDTO {

    private String nom;
    private String prenom;

    @Email
    private String email;

    private String password;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Boolean mfaEnabled;

    private Set<String> roles;
}
