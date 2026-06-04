package com.tn.softsys.blocoperatoire.dto.scores.aldrete;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AldreteResponseDTO {

    private UUID scoreId;

    private int total;

    private String decision;

    private boolean eligibleForDischarge;

    private String algorithmVersion;

    private LocalDateTime dateCalcul;
}