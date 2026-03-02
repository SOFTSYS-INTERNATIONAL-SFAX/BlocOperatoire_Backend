package com.tn.softsys.blocoperatoire.dto.surveillance;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SurveillanceResponseDTO {

    private UUID surveillanceId;
    private Float tensionArterielle;
    private Integer frequenceCardiaque;
    private Integer spo2;
    private Float temperature;
}