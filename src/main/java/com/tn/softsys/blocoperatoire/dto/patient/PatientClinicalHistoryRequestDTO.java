package com.tn.softsys.blocoperatoire.dto.patient;

import com.tn.softsys.blocoperatoire.domain.PatientClinicalHistoryCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientClinicalHistoryRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le resume clinique est obligatoire")
    private String summary;

    @NotNull(message = "La categorie clinique est obligatoire")
    private PatientClinicalHistoryCategory category;

    @NotNull(message = "La date clinique est obligatoire")
    private LocalDate eventDate;
}
