package com.tn.softsys.blocoperatoire.security;

import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.stream.Stream;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Construire la liste des authorities
        List<SimpleGrantedAuthority> authorities =
                user.getRoles().stream()
                        .flatMap(role -> {

                            // ROLE_XXX
                            List<SimpleGrantedAuthority> roleAuthority =
                                    List.of(new SimpleGrantedAuthority(
                                            "ROLE_" + role.getNom()
                                    ));

                            // Permissions du rôle
                            List<SimpleGrantedAuthority> permissionAuthorities =
                                    role.getPermissions().stream()
                                            .map(permission ->
                                                    new SimpleGrantedAuthority(
                                                            permission.getCode()
                                                    )
                                            )
                                            .toList();

                            return Stream.concat(
                                    roleAuthority.stream(),
                                    permissionAuthorities.stream()
                            );
                        })
                        .distinct()
                        .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                user.getAccountNonLocked(),
                authorities
        );
    }
}
