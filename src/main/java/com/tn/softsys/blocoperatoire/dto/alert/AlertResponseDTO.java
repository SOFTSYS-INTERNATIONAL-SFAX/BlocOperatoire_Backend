package com.tn.softsys.blocoperatoire.dto.alert;

import com.tn.softsys.blocoperatoire.domain.AlertSeverity;
import com.tn.softsys.blocoperatoire.domain.AlertType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AlertResponseDTO {

    private UUID alertId;
    private AlertType type;
    private AlertSeverity severity;

    private String title;
    private String titleEn;
    private String titleAr;
    private String message;
    private String messageEn;
    private String messageAr;

    private String patientLabel;
    private String patientLabelEn;
    private String patientLabelAr;

    private String interventionLabel;
    private String interventionLabelEn;
    private String interventionLabelAr;

    private String roomLabel;
    private String roomLabelEn;
    private String roomLabelAr;

    private UUID interventionId;
    private UUID salleId;

    private Boolean active;
    private Boolean acknowledged;
    private Boolean viewed;

    private UUID acknowledgedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime viewedAt;
}
