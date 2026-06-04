package com.tn.softsys.blocoperatoire.dto.patient;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientArchiveTraceDTO {

    private UUID patientId;
    private String patientLabel;
    private LocalDateTime archivedAt;
    private UUID archivedByUserId;
    private String archivedBy;
    private String reason;
}
