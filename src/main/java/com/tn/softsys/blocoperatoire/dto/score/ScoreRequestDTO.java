package com.tn.softsys.blocoperatoire.dto.score;

import com.tn.softsys.blocoperatoire.domain.ScoreType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ScoreRequestDTO {

    @NotNull
    private ScoreType type;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer valeur;

    @NotNull
    private UUID interventionId;
}