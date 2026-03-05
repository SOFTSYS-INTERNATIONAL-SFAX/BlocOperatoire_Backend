package com.tn.softsys.blocoperatoire.dto.scores.sofa;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SofaRequestDTO {

    /* ================= RESPIRATORY ================= */

    @NotNull
    @Positive
    private Double pao2; // mmHg

    @NotNull
    @DecimalMin("0.21")
    @DecimalMax("1.0")
    private Double fio2; // fraction (0.21 - 1.0)

    private boolean mechanicalVentilation;

    /* ================= COAGULATION ================= */

    @NotNull
    @Min(0)
    private Integer platelets;

    /* ================= LIVER ================= */

    @NotNull
    @DecimalMin("0.0")
    private Double bilirubin;

    /* ================= CARDIOVASCULAR ================= */

    @NotNull
    private Double map;

    private Double dopamine;        // mcg/kg/min
    private Double dobutamine;      // mcg/kg/min
    private Double epinephrine;     // mcg/kg/min
    private Double norepinephrine;  // mcg/kg/min

    /* ================= NEUROLOGICAL ================= */

    @NotNull
    @Min(3)
    @Max(15)
    private Integer gcs;

    /* ================= RENAL ================= */

    @NotNull
    @DecimalMin("0.0")
    private Double creatinine;

    private Double urineOutput24h;  // mL/day
    private String justification;
    /* ================= RELATION ================= */

    @NotNull
    private UUID interventionId;
}