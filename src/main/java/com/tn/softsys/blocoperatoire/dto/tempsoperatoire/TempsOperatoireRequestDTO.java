package com.tn.softsys.blocoperatoire.dto.tempsoperatoire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TempsOperatoireRequestDTO {

    @NotNull
    private UUID interventionId;

    private LocalDateTime entreeBloc;
    private LocalDateTime debutAnesthesie;
    private LocalDateTime incision;
    private LocalDateTime finActe;
    private LocalDateTime sortieSalle;
}