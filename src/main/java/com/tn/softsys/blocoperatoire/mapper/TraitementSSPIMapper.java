package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.TraitementSSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.sspi.TraitementSSPIResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TraitementSSPIMapper {

    public TraitementSSPIResponseDTO toDTO(TraitementSSPI entity) {
        return TraitementSSPIResponseDTO.builder()
                .traitementId(entity.getTraitementId())
                .sspiId(entity.getSspi().getSspiId())
                .nom(entity.getNom())
                .dose(entity.getDose())
                .voieAdministration(entity.getVoieAdministration())
                .heureAdministration(entity.getHeureAdministration())
                .observations(entity.getObservations())
                .administreParUserId(entity.getAdministrePar() != null ? entity.getAdministrePar().getUserId() : null)
                .administreParName(buildUserLabel(entity.getAdministrePar()))
                .build();
    }

    private String buildUserLabel(User user) {
        if (user == null) {
            return null;
        }

        String fullName = ((user.getPrenom() != null ? user.getPrenom() : "") + " " +
                (user.getNom() != null ? user.getNom() : "")).trim();

        return fullName.isBlank() ? user.getEmail() : "Dr. " + fullName;
    }
}
