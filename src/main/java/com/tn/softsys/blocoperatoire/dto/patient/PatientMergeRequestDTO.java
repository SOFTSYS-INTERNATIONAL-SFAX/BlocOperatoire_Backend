package com.tn.softsys.blocoperatoire.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMergeRequestDTO {

    @NotNull(message = "Le patient cible est obligatoire")
    private UUID targetPatientId;

    @NotNull(message = "Le patient source est obligatoire")
    private UUID sourcePatientId;

    @NotBlank(message = "Le motif de fusion est obligatoire")
    private String reason;
}
