package com.tn.softsys.blocoperatoire.dto.patient;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientChangeLogDTO {

    private UUID id;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
    private UUID changedByUserId;
    private String changedBy;
}