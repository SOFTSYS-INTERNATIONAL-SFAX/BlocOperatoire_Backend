package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Deces;
import com.tn.softsys.blocoperatoire.dto.deces.DecesResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DecesMapper {

    public DecesResponseDTO toDTO(Deces entity) {
        String patientNomComplet = null;
        String patientMrn = null;

        if (entity.getIntervention() != null && entity.getIntervention().getPatient() != null) {
            var patient = entity.getIntervention().getPatient();
            String prenom = patient.getPrenom() != null ? patient.getPrenom().trim() : "";
            String nom = patient.getNom() != null ? patient.getNom().trim() : "";
            patientNomComplet = (prenom + " " + nom).trim();
            patientMrn = patient.getMrn();
        }

        return DecesResponseDTO.builder()
                .decesId(entity.getDecesId())
                .interventionId(entity.getIntervention() != null ? entity.getIntervention().getInterventionId() : null)
                .dateDeces(entity.getDateDeces())
                .cause(entity.getCause())
                .constatPar(entity.getConstatPar())
                .patientId(
                        entity.getIntervention() != null && entity.getIntervention().getPatient() != null
                                ? entity.getIntervention().getPatient().getPatientId()
                                : null
                )
                .patientNomComplet(patientNomComplet)
                .patientMrn(patientMrn)
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
                .build();
    }
}
