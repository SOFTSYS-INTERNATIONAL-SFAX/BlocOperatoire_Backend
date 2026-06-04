package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponseDTO toDTO(IncidentSSPI entity) {
        return IncidentResponseDTO.builder()
                .incidentId(entity.getIncidentId())
                .sspiId(entity.getSspi().getSspiId())
                .type(entity.getType())
                .gravite(entity.getGravite())
                .description(entity.getDescription())
                .action(entity.getAction())
                .declaredAt(entity.getDeclaredAt())
                .declaredByUserId(entity.getDeclaredBy() != null ? entity.getDeclaredBy().getUserId() : null)
                .declaredByName(buildUserLabel(entity.getDeclaredBy()))
                .resolu(entity.getResolu())
                .dateResolution(entity.getDateResolution())
                .resolvedByUserId(entity.getResolvedBy() != null ? entity.getResolvedBy().getUserId() : null)
                .resolvedByName(buildUserLabel(entity.getResolvedBy()))
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
