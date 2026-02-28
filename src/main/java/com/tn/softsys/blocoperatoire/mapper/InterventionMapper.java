package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.dto.intervention.*;
import org.springframework.stereotype.Component;

@Component
public class InterventionMapper {

    public Intervention toEntity(InterventionRequestDTO dto) {
        return Intervention.builder()
                .urgenceOMS(dto.getUrgenceOMS())
                .statut(dto.getStatut())
                .dureePrevue(dto.getDureePrevue())
                .dureeReelle(dto.getDureeReelle())
                .codeActe(dto.getCodeActe())
                .build();
    }

    public InterventionResponseDTO toDTO(Intervention entity) {
        return InterventionResponseDTO.builder()
                .interventionId(entity.getInterventionId())
                .urgenceOMS(entity.getUrgenceOMS())
                .statut(entity.getStatut())
                .dureePrevue(entity.getDureePrevue())
                .dureeReelle(entity.getDureeReelle())
                .codeActe(entity.getCodeActe())
                .patientId(entity.getPatient().getPatientId())
                .salleId(entity.getSalle() != null ? entity.getSalle().getSalleId() : null)
                .planningId(entity.getPlanningBloc() != null ? entity.getPlanningBloc().getPlanningId() : null)
                .build();
    }

    public void updateEntity(Intervention entity, InterventionRequestDTO dto) {
        entity.setUrgenceOMS(dto.getUrgenceOMS());
        entity.setStatut(dto.getStatut());
        entity.setDureePrevue(dto.getDureePrevue());
        entity.setDureeReelle(dto.getDureeReelle());
        entity.setCodeActe(dto.getCodeActe());
    }
}