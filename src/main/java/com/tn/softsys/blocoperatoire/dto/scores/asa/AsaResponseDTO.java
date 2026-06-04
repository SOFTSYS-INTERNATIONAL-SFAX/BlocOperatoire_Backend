package com.tn.softsys.blocoperatoire.dto.scores.asa;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsaResponseDTO {

    private UUID scoreId;

    private String asaClass; // ASA_I, ASA_II, etc.

    private boolean emergency;

    private String finalClassification; // ex: ASA_III_E

    private int numericValue;

    private String algorithmVersion;

    private LocalDateTime dateCalcul;
}