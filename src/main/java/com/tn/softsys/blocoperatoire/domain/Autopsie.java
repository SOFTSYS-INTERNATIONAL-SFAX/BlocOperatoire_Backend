package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "autopsies")
public class Autopsie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID autopsieId;

    @OneToOne(optional = false)
    @JoinColumn(name = "deces_id", nullable = false, unique = true)
    private Deces deces;

    @Column(nullable = false)
    private LocalDateTime datePrevue;

    private LocalDateTime dateRealisee;

    @Column(nullable = false, length = 200)
    private String medecinLegiste;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AutopsieStatut statut;

    @Column(length = 4000)
    private String rapport;

    @Column(length = 2000)
    private String observations;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (statut == null) {
            statut = AutopsieStatut.PLANIFIEE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "autopsie", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<MorgueDocument> documents = new java.util.ArrayList<>();

}
