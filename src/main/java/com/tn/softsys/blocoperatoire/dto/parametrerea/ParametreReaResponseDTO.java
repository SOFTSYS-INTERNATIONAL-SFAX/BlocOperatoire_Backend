package com.tn.softsys.blocoperatoire.dto.parametrerea;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametreReaResponseDTO {

    private UUID paramId;
    private UUID reaId;
    private LocalDateTime dateMesure;
    private Boolean ventilation;
    private Boolean vasopresseurActif;
    private Double diurese;
}