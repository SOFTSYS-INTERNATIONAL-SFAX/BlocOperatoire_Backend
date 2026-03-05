package com.tn.softsys.blocoperatoire.dto.salle;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SalleResponseDTO {

    private UUID salleId;

    private String nom;
    private String etageBatiment;
    private String equipements;
    private Boolean active;
}