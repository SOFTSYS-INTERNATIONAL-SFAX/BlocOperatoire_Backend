package com.tn.softsys.blocoperatoire.dto.scores.gcs;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GcsResponseDTO {

    private UUID scoreId;

    private int total;

    private String gravite;

    private String algorithmVersion;

    private LocalDateTime dateCalcul;
}