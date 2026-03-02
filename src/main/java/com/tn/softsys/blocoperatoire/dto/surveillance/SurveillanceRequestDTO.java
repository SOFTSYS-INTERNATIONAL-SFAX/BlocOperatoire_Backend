package com.tn.softsys.blocoperatoire.dto.surveillance;

import lombok.Data;

import java.util.UUID;

@Data
public class SurveillanceRequestDTO {

    private UUID sspiId;
    private Float tensionArterielle;
    private Integer frequenceCardiaque;
    private Integer spo2;
    private Float temperature;
}