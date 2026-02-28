package com.tn.softsys.blocoperatoire;

import com.tn.softsys.blocoperatoire.dto.auth.AuthRequest;
import com.tn.softsys.blocoperatoire.domain.Role;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.RoleRepository;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import com.tn.softsys.blocoperatoire.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    /* =========================================================
       FIXED IP FOR RATE LIMIT
    ========================================================== */

    private static RequestPostProcessor fixedIp() {
        return request -> {
            request.setRemoteAddr("127.0.0.1");
            return request;
        };
    }

    @BeforeEach
    void setup() {

        Role adminRole = roleRepository.findByNom("ADMIN").orElseThrow();
        Role userRole = roleRepository.findByNom("CHIRURGIEN").orElseThrow();

        // Create test USER (non admin)
        if (!userRepository.existsByEmail("user@test.com")) {
            User user = User.builder()
                    .nom("User")
                    .prenom("Test")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .enabled(true)
                    .accountNonLocked(true)
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(user);
        }

        User admin = userRepository.findByEmail("admin@bloc.com").orElseThrow();
        User user = userRepository.findByEmail("user@test.com").orElseThrow();

        adminToken = jwtService.generateAccessToken(admin);
        userToken = jwtService.generateAccessToken(user);
    }

    /* =========================================================
       401 → No token
    ========================================================== */

    @Test
    void shouldReturn401_whenNoToken() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    /* =========================================================
       403 → User not admin
    ========================================================== */

    @Test
    void shouldReturn403_whenUserNotAdmin() throws Exception {

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    /* =========================================================
       200 → Admin access
    ========================================================== */

    @Test
    void shouldReturn200_whenAdminAccess() throws Exception {

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    /* =========================================================
       429 → Rate limit login
    ========================================================== */

    @Test
    void shouldReturn429_afterTooManyLoginAttempts() throws Exception {

        AuthRequest request = new AuthRequest();
        request.setEmail("admin@bloc.com");
        request.setPassword("wrongpassword");

        for (int i = 0; i < 6; i++) {

            if (i < 5) {
                mockMvc.perform(post("/api/auth/login")
                                .with(fixedIp()) // 🔥 CRITICAL FIX
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isUnauthorized());
            } else {
                mockMvc.perform(post("/api/auth/login")
                                .with(fixedIp()) // 🔥 CRITICAL FIX
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isTooManyRequests());
            }
        }
    }
}
