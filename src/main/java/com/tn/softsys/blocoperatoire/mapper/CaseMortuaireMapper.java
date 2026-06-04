package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.CaseMortuaire;
import com.tn.softsys.blocoperatoire.domain.MouvementCorps;
import com.tn.softsys.blocoperatoire.dto.casemor.CaseMortuaireResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class CaseMortuaireMapper {

    public CaseMortuaireResponseDTO toDTO(CaseMortuaire entity) {
        MouvementCorps dernierMouvement = entity.getMouvements() == null
                ? null
                : entity.getMouvements().stream()
                .filter(item -> item.getDateMouvement() != null)
                .max(Comparator.comparing(MouvementCorps::getDateMouvement))
                .orElse(null);

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

        return CaseMortuaireResponseDTO.builder()
                .caseId(entity.getCaseId())
                .numeroCase(entity.getNumeroCase())
                .occupee(entity.getOccupee())
                .morgueId(entity.getMorgue() != null ? entity.getMorgue().getMorgueId() : null)
                .morgueNom(entity.getMorgue() != null ? entity.getMorgue().getNom() : null)
                .decesId(entity.getDeces() != null ? entity.getDeces().getDecesId() : null)
                .dateDeces(entity.getDeces() != null ? entity.getDeces().getDateDeces() : null)
                .causeDeces(entity.getDeces() != null ? entity.getDeces().getCause() : null)
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
                .mouvementsCount(entity.getMouvements() != null ? entity.getMouvements().size() : 0)
                .dernierMouvementType(dernierMouvement != null ? dernierMouvement.getTypeMouvement() : null)
                .dernierMouvementDate(dernierMouvement != null ? dernierMouvement.getDateMouvement() : null)
                .build();
    }
}
