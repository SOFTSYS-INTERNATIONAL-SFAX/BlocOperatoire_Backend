package com.tn.softsys.blocoperatoire.dto.scores.eva;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaResponseDTO {

    private UUID scoreId;

    private int value;

    private String alertLevel;

    private boolean severeAlert;

    private String algorithmVersion;

    private LocalDateTime dateCalcul;
}