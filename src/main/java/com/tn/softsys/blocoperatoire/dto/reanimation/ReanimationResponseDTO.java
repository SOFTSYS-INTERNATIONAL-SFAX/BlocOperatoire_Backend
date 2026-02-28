package com.tn.softsys.blocoperatoire.dto.reanimation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReanimationResponseDTO {

    private UUID reaId;
    private UUID sspiId;
    private LocalDateTime dateEntree;
    private LocalDateTime dateSortie;

    private Boolean ventilation;
    private Boolean vasopresseurActif;
    private Float diurese;
}