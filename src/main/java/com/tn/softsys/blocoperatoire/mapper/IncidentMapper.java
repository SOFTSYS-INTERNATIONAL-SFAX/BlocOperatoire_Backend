package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponseDTO toDTO(IncidentSSPI entity) {
        return IncidentResponseDTO.builder()
                .incidentId(entity.getIncidentId())
                .type(entity.getType())
                .gravite(entity.getGravite())
                .action(entity.getAction())
                .build();
    }
}