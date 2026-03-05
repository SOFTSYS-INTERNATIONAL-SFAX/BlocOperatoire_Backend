package com.tn.softsys.blocoperatoire.dto.scores.asa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsaRequestDTO {

    private boolean brainDeadDonor;

    private boolean moribund;

    private boolean lifeThreateningDisease;

    private boolean severeSystemicDisease;

    private boolean mildSystemicDisease;

    private boolean emergency;
    /* ===================== JUSTIFICATION CLINIQUE ===================== */

    @NotBlank
    @Size(max = 2000)
    private String justification;

    @NotNull(message = "Intervention ID is required")
    private UUID interventionId;
}