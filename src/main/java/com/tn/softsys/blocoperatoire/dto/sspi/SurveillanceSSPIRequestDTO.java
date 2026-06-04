package com.tn.softsys.blocoperatoire.dto.sspi;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveillanceSSPIRequestDTO {

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
}
