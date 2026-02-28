package com.tn.softsys.blocoperatoire.dto.sspi;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SSPIRequestDTO {

    private UUID interventionId;
    private LocalDateTime heureEntree;
}