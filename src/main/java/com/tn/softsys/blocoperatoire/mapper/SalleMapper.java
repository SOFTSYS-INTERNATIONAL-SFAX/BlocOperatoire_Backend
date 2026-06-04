package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.domain.StatutSalle;
import com.tn.softsys.blocoperatoire.dto.salle.SalleRequestDTO;
import com.tn.softsys.blocoperatoire.dto.salle.SalleResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SalleMapper {

    public Salle toEntity(SalleRequestDTO dto) {
        return Salle.builder()
                .nom(dto.getNom())
                .nomEn(dto.getNomEn())
                .nomAr(dto.getNomAr())
                .idBloc(dto.getIdBloc())
                .idBlocEn(dto.getIdBlocEn())
                .idBlocAr(dto.getIdBlocAr())
                .etageBatiment(dto.getEtageBatiment())
                .etageBatimentEn(dto.getEtageBatimentEn())
                .etageBatimentAr(dto.getEtageBatimentAr())
                .equipements(dto.getEquipements())
                .active(dto.getActive())
                .statut(toStatutOrNull(dto.getStatut()))
                .build();
    }

    public SalleResponseDTO toDTO(Salle entity) {
        return SalleResponseDTO.builder()
                .salleId(entity.getSalleId())
                .nom(entity.getNom())
                .nomEn(entity.getNomEn())
                .nomAr(entity.getNomAr())
                .idBloc(entity.getIdBloc())
                .idBlocEn(entity.getIdBlocEn())
                .idBlocAr(entity.getIdBlocAr())
                .etageBatiment(entity.getEtageBatiment())
                .etageBatimentEn(entity.getEtageBatimentEn())
                .etageBatimentAr(entity.getEtageBatimentAr())
                .equipements(entity.getEquipements())
                .active(entity.getActive())
                .statut(entity.getStatut() != null ? entity.getStatut().name() : null)
                .build();
    }

    public void updateEntity(Salle entity, SalleRequestDTO dto) {
        entity.setNom(dto.getNom());
        entity.setNomEn(dto.getNomEn());
        entity.setNomAr(dto.getNomAr());
        entity.setIdBloc(dto.getIdBloc());
        entity.setIdBlocEn(dto.getIdBlocEn());
        entity.setIdBlocAr(dto.getIdBlocAr());
        entity.setEtageBatiment(dto.getEtageBatiment());
        entity.setEtageBatimentEn(dto.getEtageBatimentEn());
        entity.setEtageBatimentAr(dto.getEtageBatimentAr());
        entity.setEquipements(dto.getEquipements());
        entity.setActive(dto.getActive());
        entity.setStatut(toStatutOrNull(dto.getStatut()));
    }

    private StatutSalle toStatutOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return StatutSalle.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
