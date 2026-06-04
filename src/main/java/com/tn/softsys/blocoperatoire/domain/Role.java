package com.tn.softsys.blocoperatoire.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "roles",
        indexes = {
                @Index(name = "idx_role_nom", columnList = "nom")
        }
)
public class Role {

    /* ========================
       PRIMARY KEY
    ======================== */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private UUID roleId;

    /* ========================
       ROLE INFO
    ======================== */

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(length = 255)
    private String description;

    /* ========================
       ROLE ↔ USER (ManyToMany)
       côté inverse
    ======================== */

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    /* ========================
       ROLE ↔ PERMISSION (ManyToMany)
       côté propriétaire
    ======================== */

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /* ========================
       AUDIT FIELDS
    ======================== */

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ========================
       ENTITY LIFECYCLE
    ======================== */

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ========================
       EQUALS & HASHCODE
       recommandé par Hibernate
    ======================== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return roleId != null && roleId.equals(role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}