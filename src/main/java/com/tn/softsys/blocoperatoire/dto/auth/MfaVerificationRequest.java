package com.tn.softsys.blocoperatoire.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MfaVerificationRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;
}