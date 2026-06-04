package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Alert;
import com.tn.softsys.blocoperatoire.dto.alert.AlertResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public AlertResponseDTO toDTO(Alert entity) {
        return AlertResponseDTO.builder()
                .alertId(entity.getAlertId())
                .type(entity.getType())
                .severity(entity.getSeverity())
                .title(entity.getTitle())
                .titleEn(entity.getTitleEn())
                .titleAr(entity.getTitleAr())
                .message(entity.getMessage())
                .messageEn(entity.getMessageEn())
                .messageAr(entity.getMessageAr())
                .patientLabel(entity.getPatientLabel())
                .patientLabelEn(entity.getPatientLabelEn())
                .patientLabelAr(entity.getPatientLabelAr())
                .interventionLabel(entity.getInterventionLabel())
                .interventionLabelEn(entity.getInterventionLabelEn())
                .interventionLabelAr(entity.getInterventionLabelAr())
                .roomLabel(entity.getRoomLabel())
                .roomLabelEn(entity.getRoomLabelEn())
                .roomLabelAr(entity.getRoomLabelAr())
                .interventionId(
                        entity.getIntervention() != null ? entity.getIntervention().getInterventionId() : null
                )
                .salleId(
                        entity.getSalle() != null ? entity.getSalle().getSalleId() : null
                )
                .active(entity.getActive())
                .acknowledged(entity.getAcknowledged())
                .viewed(entity.getViewed())
                .acknowledgedByUserId(
                        entity.getAcknowledgedBy() != null ? entity.getAcknowledgedBy().getUserId() : null
                )
                .createdAt(entity.getCreatedAt())
                .acknowledgedAt(entity.getAcknowledgedAt())
                .viewedAt(entity.getViewedAt())
                .build();
    }
}
