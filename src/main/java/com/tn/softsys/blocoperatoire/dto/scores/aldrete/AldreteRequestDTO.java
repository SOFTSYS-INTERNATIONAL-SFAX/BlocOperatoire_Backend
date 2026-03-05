package com.tn.softsys.blocoperatoire.dto.scores.aldrete;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AldreteRequestDTO {

    @Min(0) @Max(2)
    private int activity;

    @Min(0) @Max(2)
    private int respiration;
    @NotBlank
    @Size(max = 2000)
    private String justification;   // 🔥 AJOUT IMPORTANT

    @Min(0) @Max(2)
    private int circulation;

    @Min(0) @Max(2)
    private int consciousness;

    @Min(0) @Max(2)
    private int oxygenation;

    @NotNull
    private UUID interventionId;
}