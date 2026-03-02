package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.dto.salle.*;
import org.springframework.stereotype.Component;

@Component
public class SalleMapper {

    public Salle toEntity(SalleRequestDTO dto) {
        return Salle.builder()
                .nom(dto.getNom())
                .type(dto.getType())
                .disponible(dto.getDisponible())
                .build();
    }

    public SalleResponseDTO toDTO(Salle entity) {
        return SalleResponseDTO.builder()
                .salleId(entity.getSalleId())
                .nom(entity.getNom())
                .type(entity.getType())
                .disponible(entity.getDisponible())
                .build();
    }

    public void updateEntity(Salle entity, SalleRequestDTO dto) {
        entity.setNom(dto.getNom());
        entity.setType(dto.getType());
        entity.setDisponible(dto.getDisponible());
    }
}