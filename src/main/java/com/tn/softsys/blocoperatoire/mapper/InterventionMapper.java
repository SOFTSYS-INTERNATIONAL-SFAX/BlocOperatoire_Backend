package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.PlanningBloc;
import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.intervention.InterventionRequestDTO;
import com.tn.softsys.blocoperatoire.dto.intervention.InterventionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class InterventionMapper {

    public Intervention toEntity(
            InterventionRequestDTO dto,
            Patient patient,
            Salle salle,
            PlanningBloc planningBloc,
            User chirurgien,
            User anesthesiste
    ) {
        return Intervention.builder()
                .nomIntervention(dto.getNomIntervention())
                .nomInterventionEn(dto.getNomInterventionEn())
                .nomInterventionAr(dto.getNomInterventionAr())
                .codeActe(dto.getCodeActe())
                .lateralite(dto.getLateralite())
                .statut(dto.getStatut())
                .priorite(dto.getPriorite())
                .urgenceOMS(dto.getUrgenceOMS())
                .dureePrevue(dto.getDureePrevue())
                .dureeReelle(dto.getDureeReelle())
                .diagnostic(dto.getDiagnostic())
                .dateIntervention(dto.getDateIntervention())
                .heureDebut(dto.getHeureDebut())
                .patient(patient)
                .salle(salle)
                .planningBloc(planningBloc)
                .chirurgien(chirurgien)
                .anesthesiste(anesthesiste)
                .build();
    }

    public void updateEntity(
            Intervention intervention,
            InterventionRequestDTO dto,
            Patient patient,
            Salle salle,
            PlanningBloc planningBloc,
            User chirurgien,
            User anesthesiste
    ) {
        intervention.setNomIntervention(dto.getNomIntervention());
        intervention.setNomInterventionEn(dto.getNomInterventionEn());
        intervention.setNomInterventionAr(dto.getNomInterventionAr());
        intervention.setCodeActe(dto.getCodeActe());
        intervention.setLateralite(dto.getLateralite());
        intervention.setStatut(dto.getStatut());
        intervention.setPriorite(dto.getPriorite());
        intervention.setUrgenceOMS(dto.getUrgenceOMS());
        intervention.setDureePrevue(dto.getDureePrevue());
        intervention.setDureeReelle(dto.getDureeReelle());
        intervention.setDiagnostic(dto.getDiagnostic());
        intervention.setDateIntervention(dto.getDateIntervention());
        intervention.setHeureDebut(dto.getHeureDebut());

        intervention.setPatient(patient);
        intervention.setSalle(salle);
        intervention.setPlanningBloc(planningBloc);
        intervention.setChirurgien(chirurgien);
        intervention.setAnesthesiste(anesthesiste);
    }

    public InterventionResponseDTO toResponse(Intervention intervention) {
        return InterventionResponseDTO.builder()
                .interventionId(intervention.getInterventionId())
                .catalogId(
                        intervention.getCatalog() != null
                                ? intervention.getCatalog().getCatalogId()
                                : null
                )
                .nomIntervention(intervention.getNomIntervention())
                .nomInterventionEn(resolveInterventionLabelEn(intervention))
                .nomInterventionAr(resolveInterventionLabelAr(intervention))
                .codeActe(intervention.getCodeActe())
                .lateralite(intervention.getLateralite())
                .statut(intervention.getStatut())
                .priorite(intervention.getPriorite())
                .urgenceOMS(intervention.getUrgenceOMS())
                .dureePrevue(intervention.getDureePrevue())
                .dureeReelle(intervention.getDureeReelle())
                .diagnostic(intervention.getDiagnostic())
                .dateIntervention(intervention.getDateIntervention())
                .heureDebut(intervention.getHeureDebut())
                .patientId(
                        intervention.getPatient() != null
                                ? intervention.getPatient().getPatientId()
                                : null
                )
                .salleId(
                        intervention.getSalle() != null
                                ? intervention.getSalle().getSalleId()
                                : null
                )
                .planningId(
                        intervention.getPlanningBloc() != null
                                ? intervention.getPlanningBloc().getPlanningId()
                                : null
                )
                .chirurgienId(
                        intervention.getChirurgien() != null
                                ? intervention.getChirurgien().getUserId()
                                : null
                )
                .anesthesisteId(
                        intervention.getAnesthesiste() != null
                                ? intervention.getAnesthesiste().getUserId()
                                : null
                )
                .hasTempsOperatoire(intervention.getTempsOperatoire() != null)
                .hasSSPI(intervention.getSspi() != null)
                .hasDeces(intervention.getDeces() != null)
                .build();
    }

    private String resolveInterventionLabelEn(Intervention intervention) {
        if (intervention.getNomInterventionEn() != null && !intervention.getNomInterventionEn().isBlank()) {
            return intervention.getNomInterventionEn().trim();
        }

        if (intervention.getCatalog() != null
                && intervention.getCatalog().getDesignationEn() != null
                && !intervention.getCatalog().getDesignationEn().isBlank()) {
            return intervention.getCatalog().getDesignationEn().trim();
        }

        return intervention.getNomIntervention();
    }

    private String resolveInterventionLabelAr(Intervention intervention) {
        if (intervention.getNomInterventionAr() != null && !intervention.getNomInterventionAr().isBlank()) {
            return intervention.getNomInterventionAr().trim();
        }

        if (intervention.getCatalog() != null
                && intervention.getCatalog().getDesignationAr() != null
                && !intervention.getCatalog().getDesignationAr().isBlank()) {
            return intervention.getCatalog().getDesignationAr().trim();
        }

        return intervention.getNomIntervention();
    }
}
