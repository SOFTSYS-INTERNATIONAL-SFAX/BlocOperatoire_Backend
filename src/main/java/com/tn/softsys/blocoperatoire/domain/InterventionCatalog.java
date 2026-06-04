package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "intervention_catalog")
public class InterventionCatalog {

    @Id
    @Column(name = "catalog_id", nullable = false, updatable = false)
    private UUID catalogId;

    @Column(nullable = false, length = 512)
    private String designation;

    @Column(name = "designation_en", length = 512)
    private String designationEn;

    @Column(name = "designation_ar", length = 512)
    private String designationAr;

    @Column(name = "cotation_unite", length = 32)
    private String cotationUnite;

    @Column(name = "cotation_valeur", precision = 10, scale = 2)
    private BigDecimal cotationValeur;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
