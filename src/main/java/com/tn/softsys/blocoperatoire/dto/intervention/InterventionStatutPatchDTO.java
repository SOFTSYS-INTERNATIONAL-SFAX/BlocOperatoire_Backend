package com.tn.softsys.blocoperatoire.dto.intervention;

import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterventionStatutPatchDTO {
    @NotNull
    private StatutIntervention statut;
}
