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

import java.util.Set;

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
    private final HttpServletRequest httpRequest;

    /* =====================================================
       LOGIN
    ===================================================== */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request
    ) {

        String ip = httpRequest.getRemoteAddr();

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
                    "Invalid credentials",
                    ip
            );

            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getMfaEnabled())) {

            auditLogService.log(
                    user,
                    "LOGIN_MFA_REQUIRED",
                    "AUTHENTICATION",
                    user.getUserId(),
                    "MFA required before issuing tokens",
                    ip
            );

            return ResponseEntity.status(206)
                    .body(
                            AuthResponse.builder()
                                    .mfaRequired(true)
                                    .tokenType("Bearer")
                                    .build()
                    );
        }

        return generateTokens(user, "LOGIN_SUCCESS", ip);
    }

    /* =====================================================
       VERIFY MFA
    ===================================================== */

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponse> verifyMfa(
            @Valid @RequestBody MfaVerificationRequest request
    ) {

        String ip = httpRequest.getRemoteAddr();

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getMfaEnabled())) {
            return ResponseEntity.badRequest().build();
        }

        boolean valid = mfaService.verifyCode(
                user.getMfaSecret(),
                request.getCode()
        );

        if (!valid) {

            auditLogService.logFailedAttempt(
                    request.getEmail(),
                    "MFA_FAILED",
                    "AUTHENTICATION",
                    "Invalid MFA code",
                    ip
            );

            return ResponseEntity.status(401).build();
        }

        return generateTokens(user, "MFA_SUCCESS", ip);
    }

    /* =====================================================
       REGISTER
    ===================================================== */

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        String ip = httpRequest.getRemoteAddr();

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        Role userRole = roleRepository.findByNom("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        auditLogService.log(
                user,
                "REGISTER_SUCCESS",
                "AUTHENTICATION",
                user.getUserId(),
                "New user registered",
                ip
        );

        return generateTokens(user, "REGISTER_LOGIN_SUCCESS", ip);
    }

    /* =====================================================
       REFRESH TOKEN
    ===================================================== */

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {

        String ip = httpRequest.getRemoteAddr();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String requestRefreshToken = authHeader.substring(7);

        RefreshToken refreshToken =
                refreshTokenService.findByToken(requestRefreshToken);

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);

        auditLogService.log(
                user,
                "REFRESH_TOKEN",
                "AUTHENTICATION",
                user.getUserId(),
                "Access token refreshed",
                ip
        );

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken.getToken())
                        .tokenType("Bearer")
                        .mfaRequired(false)
                        .build()
        );
    }

    /* =====================================================
       LOGOUT
    ===================================================== */

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader
    ) {

        String ip = httpRequest.getRemoteAddr();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = authHeader.substring(7);

        RefreshToken token =
                refreshTokenService.findByToken(refreshToken);

        User user = token.getUser();

        refreshTokenService.deleteByToken(refreshToken);

        auditLogService.log(
                user,
                "LOGOUT",
                "AUTHENTICATION",
                user.getUserId(),
                "User logged out and refresh token invalidated",
                ip
        );

        return ResponseEntity.ok().build();
    }

    /* =====================================================
       GENERATE TOKENS
    ===================================================== */

    private ResponseEntity<AuthResponse> generateTokens(
            User user,
            String auditAction,
            String ip
    ) {

        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getEmail());

        auditLogService.log(
                user,
                auditAction,
                "AUTHENTICATION",
                user.getUserId(),
                "Access and refresh tokens generated",
                ip
        );

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .mfaRequired(false)
                        .tokenType("Bearer")
                        .build()
        );
    }
}