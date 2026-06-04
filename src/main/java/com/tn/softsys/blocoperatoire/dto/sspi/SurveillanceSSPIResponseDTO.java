package com.tn.softsys.blocoperatoire.dto.sspi;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SurveillanceSSPIResponseDTO {

    private UUID surveillanceId;
    private UUID sspiId;
    private LocalDateTime dateMesure;
    private Integer frequenceCardiaque;
    private Integer paSystolique;
    private Integer paDiastolique;
    private Integer frequenceRespiratoire;
    private Integer spo2;
    private Integer temperatureDixieme;
    private Integer douleurEva;
    private Integer scoreConscience;
    private String observations;
    private UUID mesureeParUserId;
    private String mesureeParName;
}
