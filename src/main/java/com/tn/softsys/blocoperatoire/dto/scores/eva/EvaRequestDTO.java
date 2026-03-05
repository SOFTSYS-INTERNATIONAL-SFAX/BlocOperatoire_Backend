package com.tn.softsys.blocoperatoire.dto.scores.eva;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaRequestDTO {

    @Min(value = 0, message = "EVA must be >= 0")
    @Max(value = 10, message = "EVA must be <= 10")
    private int value;

    @NotNull(message = "Intervention ID is required")
    private UUID interventionId;
    @NotBlank
    @Size(max = 2000)
    private String justification;   // 🔥 AJOUT IMPORTANT
}