package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.SurveillanceSSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.sspi.SurveillanceSSPIResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SurveillanceSSPIMapper {

    public SurveillanceSSPIResponseDTO toDTO(SurveillanceSSPI entity) {
        return SurveillanceSSPIResponseDTO.builder()
                .surveillanceId(entity.getSurveillanceId())
                .sspiId(entity.getSspi().getSspiId())
                .dateMesure(entity.getDateMesure())
                .frequenceCardiaque(entity.getFrequenceCardiaque())
                .paSystolique(entity.getPaSystolique())
                .paDiastolique(entity.getPaDiastolique())
                .frequenceRespiratoire(entity.getFrequenceRespiratoire())
                .spo2(entity.getSpo2())
                .temperatureDixieme(entity.getTemperatureDixieme())
                .douleurEva(entity.getDouleurEva())
                .scoreConscience(entity.getScoreConscience())
                .observations(entity.getObservations())
                .mesureeParUserId(entity.getMesureePar() != null ? entity.getMesureePar().getUserId() : null)
                .mesureeParName(buildUserLabel(entity.getMesureePar()))
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
