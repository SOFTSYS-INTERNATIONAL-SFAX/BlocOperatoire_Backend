package com.tn.softsys.blocoperatoire.dto.sspi;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SSPIRequestDTO {

    @NotNull
    private UUID interventionId;

    private LocalDateTime heureEntree;
    private String posteCode;
}