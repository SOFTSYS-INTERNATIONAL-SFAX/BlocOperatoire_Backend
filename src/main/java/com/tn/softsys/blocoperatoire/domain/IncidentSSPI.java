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
@Table(name = "incident_sspi")
public class IncidentSSPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID incidentId;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 50)
    private String gravite;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String action;

    @Column(nullable = false)
    private LocalDateTime declaredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declared_by_user_id")
    private User declaredBy;

    @Column(nullable = false)
    private Boolean resolu;

    private LocalDateTime dateResolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_user_id")
    private User resolvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sspi_id", nullable = false)
    private SSPI sspi;
}
