package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

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

    private String type;
    private String gravite;
    private String action;

    @ManyToOne
    @JoinColumn(name = "sspi_id", nullable = false)
    private SSPI sspi;
}
