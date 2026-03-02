package com.tn.softsys.blocoperatoire.dto.reanimation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReanimationRequestDTO {

    @NotNull
    private UUID sspiId;

    @NotNull
    private LocalDateTime dateEntree;

    private LocalDateTime dateSortie;

    @NotNull
    private Boolean ventilation;

    @NotNull
    private Boolean vasopresseurActif;

    @NotNull
    private Float diurese;
}