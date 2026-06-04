package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Autopsie;
import com.tn.softsys.blocoperatoire.dto.autopsie.AutopsieResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AutopsieMapper {

    public AutopsieResponseDTO toDTO(Autopsie entity) {
        String patientNomComplet = null;
        String patientMrn = null;

        if (entity.getDeces() != null
                && entity.getDeces().getIntervention() != null
                && entity.getDeces().getIntervention().getPatient() != null) {

            var patient = entity.getDeces().getIntervention().getPatient();
            String prenom = patient.getPrenom() != null ? patient.getPrenom().trim() : "";
            String nom = patient.getNom() != null ? patient.getNom().trim() : "";
            patientNomComplet = (prenom + " " + nom).trim();
            patientMrn = patient.getMrn();
        }

        return AutopsieResponseDTO.builder()
                .autopsieId(entity.getAutopsieId())
                .decesId(entity.getDeces() != null ? entity.getDeces().getDecesId() : null)
                .interventionId(
                        entity.getDeces() != null && entity.getDeces().getIntervention() != null
                                ? entity.getDeces().getIntervention().getInterventionId()
                                : null
                )
                .patientId(
                        entity.getDeces() != null
                                && entity.getDeces().getIntervention() != null
                                && entity.getDeces().getIntervention().getPatient() != null
                                ? entity.getDeces().getIntervention().getPatient().getPatientId()
                                : null
                )
                .patientNomComplet(patientNomComplet)
                .patientMrn(patientMrn)
                .caseId(
                        entity.getDeces() != null && entity.getDeces().getCaseMortuaire() != null
                                ? entity.getDeces().getCaseMortuaire().getCaseId()
                                : null
                )
                .numeroCase(
                        entity.getDeces() != null && entity.getDeces().getCaseMortuaire() != null
                                ? entity.getDeces().getCaseMortuaire().getNumeroCase()
                                : null
                )
                .morgueId(
                        entity.getDeces() != null
                                && entity.getDeces().getCaseMortuaire() != null
                                && entity.getDeces().getCaseMortuaire().getMorgue() != null
                                ? entity.getDeces().getCaseMortuaire().getMorgue().getMorgueId()
                                : null
                )
                .morgueNom(
                        entity.getDeces() != null
                                && entity.getDeces().getCaseMortuaire() != null
                                && entity.getDeces().getCaseMortuaire().getMorgue() != null
                                ? entity.getDeces().getCaseMortuaire().getMorgue().getNom()
                                : null
                )
                .datePrevue(entity.getDatePrevue())
                .dateRealisee(entity.getDateRealisee())
                .medecinLegiste(entity.getMedecinLegiste())
                .statut(entity.getStatut())
                .rapport(entity.getRapport())
                .observations(entity.getObservations())
                .build();
    }
}
