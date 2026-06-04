package com.tn.softsys.blocoperatoire.dto.scores.sofa;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SofaRequestDTO {

    @NotNull
    @Positive
    private Double pao2;

    @NotNull
    @DecimalMin("0.21")
    @DecimalMax("1.0")
    private Double fio2;

    private boolean mechanicalVentilation;

    @NotNull
    @Min(0)
    private Integer platelets;

    @NotNull
    @DecimalMin("0.0")
    private Double bilirubin;

    @NotNull
    private Double map;

    private Double dopamine;
    private Double dobutamine;
    private Double epinephrine;
    private Double norepinephrine;

    @NotNull
    @Min(3)
    @Max(15)
    private Integer gcs;

    @NotNull
    @DecimalMin("0.0")
    private Double creatinine;

    private Double urineOutput24h;
    private String justification;

    @NotNull
    private UUID patientId;

    private UUID interventionId;
}
