package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.auth.*;
import com.tn.softsys.blocoperatoire.repository.*;
import com.tn.softsys.blocoperatoire.security.JwtService;
import com.tn.softsys.blocoperatoire.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;
    private final MfaService mfaService;

    /* ================= LOGIN ================= */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest
    ) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {

            auditLogService.logFailedAttempt(
                    request.getEmail(),
                    "LOGIN_FAILED",
                    "AUTHENTICATION",
                    httpRequest.getRemoteAddr(),
                    "Invalid credentials"
            );

            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getMfaEnabled())) {

            auditLogService.log(
                    user,
                    "MFA_REQUIRED",
                    "AUTHENTICATION",
                    httpRequest.getRemoteAddr(),
                    "Second factor required"
            );

            return ResponseEntity.status(206)
                    .body(AuthResponse.builder()
                            .mfaRequired(true)
                            .tokenType("Bearer")
                            .build());
        }

        return generateTokens(user, httpRequest, "LOGIN_SUCCESS");
    }

    /* ================= REGISTER ================= */

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // 🔥 MODIFICATION ICI → ADMIN AU LIEU DE USER
        Role adminRole = roleRepository.findByNom("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(user);

        return generateTokens(user, httpRequest, "REGISTER");
    }

    /* ================= REFRESH ================= */

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String requestRefreshToken = authHeader.substring(7);

        RefreshToken refreshToken =
                refreshTokenService.findByToken(requestRefreshToken);

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken.getToken())
                        .tokenType("Bearer")
                        .build()
        );
    }

    /* ================= LOGOUT ================= */

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = authHeader.substring(7);

        refreshTokenService.deleteByToken(refreshToken);

        return ResponseEntity.ok().build();
    }

    /* ================= TOKEN GENERATOR ================= */

    private ResponseEntity<AuthResponse> generateTokens(
            User user,
            HttpServletRequest request,
            String action
    ) {

        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getEmail());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .tokenType("Bearer")
                        .build()
        );
    }
}