package com.tn.softsys.blocoperatoire.dto.oms;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OmsSignOutResponseDTO {
    private UUID checklistId;
    private UUID signOutId;
    private UUID patientId;
    private UUID interventionId;
    private Boolean interventionRecorded;
    private Boolean instrumentsCountCorrect;
    private Boolean specimensLabeled;
    private Boolean recoveryPlanConfirmed;
    private String equipmentProblems;
    private Boolean surgeonValidated;
    private Boolean anesthesisteValidated;
    private Boolean infirmierValidated;
    private String surgeonValidatedByName;
    private String anesthesisteValidatedByName;
    private String infirmierValidatedByName;
    private LocalDateTime surgeonValidatedAt;
    private LocalDateTime anesthesisteValidatedAt;
    private LocalDateTime infirmierValidatedAt;
    private UUID completedByUserId;
    private String completedByName;
    private LocalDateTime completedAt;
}
