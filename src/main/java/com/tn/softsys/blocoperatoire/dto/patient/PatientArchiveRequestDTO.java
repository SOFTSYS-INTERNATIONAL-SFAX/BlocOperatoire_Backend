package com.tn.softsys.blocoperatoire.dto.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientArchiveRequestDTO {

    @NotBlank(message = "Le motif d'archivage est obligatoire")
    private String reason;
}
