package com.tn.softsys.blocoperatoire.dto.oms;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OmsTimeOutResponseDTO {
    private UUID checklistId;
    private UUID timeOutId;
    private UUID patientId;
    private UUID interventionId;
    private Boolean teamIntroduced;
    private Boolean patientNameConfirmed;
    private Boolean interventionConfirmed;
    private Boolean siteConfirmed;
    private Boolean antibioticProphylaxisGiven;
    private Boolean imagingDisplayed;
    private String criticalEventsSurgeon;
    private String criticalEventsAnesthesia;
    private UUID completedByUserId;
    private String completedByName;
    private LocalDateTime completedAt;
}
