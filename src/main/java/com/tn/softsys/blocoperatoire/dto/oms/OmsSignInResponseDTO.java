package com.tn.softsys.blocoperatoire.dto.oms;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OmsSignInResponseDTO {
    private UUID checklistId;
    private UUID signInId;
    private UUID patientId;
    private UUID interventionId;
    private Boolean patientIdentityConfirmed;
    private Boolean siteMarked;
    private Boolean anesthesiaMachineChecked;
    private Boolean pulseOximeterWorking;
    private Boolean difficultAirwayRisk;
    private Boolean aspirationRisk;
    private Boolean hemorrhageRisk;
    private Boolean bloodProductsAvailable;
    private String allergies;
    private UUID completedByUserId;
    private String completedByName;
    private LocalDateTime completedAt;
}
