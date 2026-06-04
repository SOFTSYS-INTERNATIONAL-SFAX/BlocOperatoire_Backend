package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.dto.mouvement.MouvementCorpsResponseDTO;
import com.tn.softsys.blocoperatoire.domain.MouvementCorps;
import org.springframework.stereotype.Component;

@Component
public class MouvementCorpsMapper {

    public MouvementCorpsResponseDTO toDTO(MouvementCorps entity) {
        String patientNomComplet = null;
        String patientMrn = null;

        if (entity.getCaseMortuaire() != null
                && entity.getCaseMortuaire().getDeces() != null
                && entity.getCaseMortuaire().getDeces().getIntervention() != null
                && entity.getCaseMortuaire().getDeces().getIntervention().getPatient() != null) {

            var patient = entity.getCaseMortuaire().getDeces().getIntervention().getPatient();
            String prenom = patient.getPrenom() != null ? patient.getPrenom().trim() : "";
            String nom = patient.getNom() != null ? patient.getNom().trim() : "";
            patientNomComplet = (prenom + " " + nom).trim();
            patientMrn = patient.getMrn();
        }

        return MouvementCorpsResponseDTO.builder()
                .mouvementId(entity.getMouvementId())
                .caseId(entity.getCaseMortuaire() != null ? entity.getCaseMortuaire().getCaseId() : null)
                .numeroCase(entity.getCaseMortuaire() != null ? entity.getCaseMortuaire().getNumeroCase() : null)
                .morgueId(
                        entity.getCaseMortuaire() != null && entity.getCaseMortuaire().getMorgue() != null
                                ? entity.getCaseMortuaire().getMorgue().getMorgueId()
                                : null
                )
                .morgueNom(
                        entity.getCaseMortuaire() != null && entity.getCaseMortuaire().getMorgue() != null
                                ? entity.getCaseMortuaire().getMorgue().getNom()
                                : null
                )
                .decesId(
                        entity.getCaseMortuaire() != null && entity.getCaseMortuaire().getDeces() != null
                                ? entity.getCaseMortuaire().getDeces().getDecesId()
                                : null
                )
                .interventionId(
                        entity.getCaseMortuaire() != null
                                && entity.getCaseMortuaire().getDeces() != null
                                && entity.getCaseMortuaire().getDeces().getIntervention() != null
                                ? entity.getCaseMortuaire().getDeces().getIntervention().getInterventionId()
                                : null
                )
                .patientId(
                        entity.getCaseMortuaire() != null
                                && entity.getCaseMortuaire().getDeces() != null
                                && entity.getCaseMortuaire().getDeces().getIntervention() != null
                                && entity.getCaseMortuaire().getDeces().getIntervention().getPatient() != null
                                ? entity.getCaseMortuaire().getDeces().getIntervention().getPatient().getPatientId()
                                : null
                )
                .patientNomComplet(patientNomComplet)
                .patientMrn(patientMrn)
                .dateMouvement(entity.getDateMouvement())
                .typeMouvement(entity.getTypeMouvement())
                .build();
    }
}
