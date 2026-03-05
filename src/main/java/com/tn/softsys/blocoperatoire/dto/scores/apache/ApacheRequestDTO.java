package com.tn.softsys.blocoperatoire.dto.scores.apache;

import com.tn.softsys.blocoperatoire.domain.scores.apache.ChronicHealthStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApacheRequestDTO {

    /* ===================== PARAMÈTRES PHYSIOLOGIQUES ===================== */

    @DecimalMin("30.0")
    @DecimalMax("45.0")
    private double temperature;

    @Min(20)
    @Max(200)
    private int map;

    @Min(20)
    @Max(250)
    private int heartRate;

    @Min(5)
    @Max(80)
    private int respiratoryRate;

    @Min(20)
    @Max(800)
    private int pao2;

    @DecimalMin("6.8")
    @DecimalMax("7.8")
    private double ph;

    @Min(100)
    @Max(200)
    private int sodium;

    @DecimalMin("1.0")
    @DecimalMax("10.0")
    private double potassium;

    @DecimalMin("0.1")
    @DecimalMax("20.0")
    private double creatinine;

    @DecimalMin("5.0")
    @DecimalMax("70.0")
    private double hematocrit;

    @DecimalMin("0.1")
    @DecimalMax("100.0")
    private double wbc;

    @Min(3)
    @Max(15)
    private int gcs;

    @Min(0)
    @Max(120)
    private int age;

    /* ===================== JUSTIFICATION CLINIQUE ===================== */

    @NotBlank
    @Size(max = 2000)
    private String justification;

    /* ===================== STATUT CHRONIQUE ===================== */

    @NotNull
    private ChronicHealthStatus chronicHealthStatus;

    /* ===================== INTERVENTION ===================== */

    @NotNull
    private UUID interventionId;
}