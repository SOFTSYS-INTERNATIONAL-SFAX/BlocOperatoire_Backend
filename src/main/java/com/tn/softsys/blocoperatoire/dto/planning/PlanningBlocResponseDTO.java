package com.tn.softsys.blocoperatoire.dto.planning;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PlanningBlocResponseDTO {

    private UUID planningId;
    private LocalDateTime date;
    private Integer nombreInterventions;
}