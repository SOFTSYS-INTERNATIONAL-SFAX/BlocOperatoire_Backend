package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.TempsOperatoire;
import com.tn.softsys.blocoperatoire.dto.tempsoperatoire.TempsOperatoireResponseDTO;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TempsOperatoireMapper {

    public TempsOperatoireResponseDTO toDTO(TempsOperatoire entity) {

        TempsOperatoireResponseDTO dto = new TempsOperatoireResponseDTO();

        dto.setTempsId(entity.getTempsId());
        dto.setInterventionId(
                entity.getIntervention().getInterventionId());

        dto.setEntreeBloc(entity.getEntreeBloc());
        dto.setDebutAnesthesie(entity.getDebutAnesthesie());
        dto.setIncision(entity.getIncision());
        dto.setFinActe(entity.getFinActe());
        dto.setSortieSalle(entity.getSortieSalle());

        if (entity.getIncision() != null &&
                entity.getFinActe() != null) {

            long minutes = Duration.between(
                    entity.getIncision(),
                    entity.getFinActe()
            ).toMinutes();

            dto.setDureeActeMinutes(minutes);
        }

        return dto;
    }
}