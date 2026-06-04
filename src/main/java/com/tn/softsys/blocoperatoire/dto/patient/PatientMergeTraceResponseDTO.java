package com.tn.softsys.blocoperatoire.dto.patient;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMergeTraceResponseDTO {

    private UUID id;
    private UUID targetPatientId;
    private String targetPatientLabel;
    private UUID sourcePatientId;
    private String sourcePatientLabel;
    private LocalDateTime mergedAt;
    private UUID mergedByUserId;
    private String mergedBy;
    private String reason;
    private String direction;
}
