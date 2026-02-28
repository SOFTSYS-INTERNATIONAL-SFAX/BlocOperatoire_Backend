package com.tn.softsys.blocoperatoire.dto.parametrerea;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametreReaRequestDTO {

    @NotNull
    private UUID reaId;

    @NotNull
    private LocalDateTime dateMesure;

    @NotNull
    private Boolean ventilation;

    @NotNull
    private Boolean vasopresseurActif;

    @NotNull
    private Double diurese;
}