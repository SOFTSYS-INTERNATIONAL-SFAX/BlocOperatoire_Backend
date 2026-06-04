package com.tn.softsys.blocoperatoire.dto.patient;

import com.tn.softsys.blocoperatoire.domain.PatientClinicalHistoryCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientClinicalHistoryResponseDTO {

    private UUID entryId;
    private UUID patientId;
    private String title;
    private String summary;
    private PatientClinicalHistoryCategory category;
    private LocalDate eventDate;
    private LocalDateTime createdAt;
    private UUID createdByUserId;
    private String createdBy;
}
