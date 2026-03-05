package com.tn.softsys.blocoperatoire.dto.scores.gcs;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GcsRequestDTO {

    @Min(1)
    @Max(4)
    private int eyes;

    @Min(1)
    @Max(5)
    private int verbal;

    @Min(1)
    @Max(6)
    private int motor;

    @NotBlank
    @Size(max = 2000)
    private String justification;   // 🔥 AJOUT IMPORTANT

    @NotNull
    private UUID interventionId;
}