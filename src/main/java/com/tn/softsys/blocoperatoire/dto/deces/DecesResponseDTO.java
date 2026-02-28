package com.tn.softsys.blocoperatoire.dto.deces;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DecesResponseDTO {

    private UUID decesId;
    private UUID interventionId;
    private LocalDateTime dateDeces;
    private String cause;
    private String constatPar;
}