package com.tn.softsys.blocoperatoire.dto.incident;

import lombok.Data;

import java.util.UUID;

@Data
public class IncidentRequestDTO {

    private UUID sspiId;
    private String type;
    private String gravite;
    private String action;
}
