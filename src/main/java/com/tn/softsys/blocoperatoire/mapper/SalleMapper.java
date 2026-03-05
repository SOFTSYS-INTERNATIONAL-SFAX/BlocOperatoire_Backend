package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.dto.salle.*;
import org.springframework.stereotype.Component;
@Component
public class SalleMapper {

    public Salle toEntity(SalleRequestDTO dto) {
        return Salle.builder()
                .nom(dto.getNom())
                .etageBatiment(dto.getEtageBatiment())
                .equipements(dto.getEquipements())
                .active(dto.getActive())
                .build();
    }

    public SalleResponseDTO toDTO(Salle entity) {
        return SalleResponseDTO.builder()
                .salleId(entity.getSalleId())
                .nom(entity.getNom())
                .etageBatiment(entity.getEtageBatiment())
                .equipements(entity.getEquipements())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(Salle entity, SalleRequestDTO dto) {
        entity.setNom(dto.getNom());
        entity.setEtageBatiment(dto.getEtageBatiment());
        entity.setEquipements(dto.getEquipements());
        entity.setActive(dto.getActive());
    }
}