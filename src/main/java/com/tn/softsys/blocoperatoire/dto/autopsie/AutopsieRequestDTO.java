package com.tn.softsys.blocoperatoire.dto.autopsie;

import com.tn.softsys.blocoperatoire.domain.AutopsieStatut;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AutopsieRequestDTO {

    @NotNull
    private UUID decesId;

    @NotNull
    private LocalDateTime datePrevue;

    private LocalDateTime dateRealisee;

    @NotBlank
    private String medecinLegiste;

    private AutopsieStatut statut;

    private String rapport;
    private String observations;
}
