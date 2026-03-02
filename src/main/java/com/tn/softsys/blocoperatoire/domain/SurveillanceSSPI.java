package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "surveillance_sspi")
public class SurveillanceSSPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID surveillanceId;

    private Float tensionArterielle;
    private Integer frequenceCardiaque;
    private Integer spo2;
    private Float temperature;

    @ManyToOne
    @JoinColumn(name = "sspi_id", nullable = false)
    private SSPI sspi;
}
