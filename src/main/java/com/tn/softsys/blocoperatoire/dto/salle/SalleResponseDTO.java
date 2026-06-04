package com.tn.softsys.blocoperatoire.dto.salle;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SalleResponseDTO {

    private UUID salleId;
    private String nom;
    private String nomEn;
    private String nomAr;
    private String idBloc;
    private String idBlocEn;
    private String idBlocAr;
    private String etageBatiment;
    private String etageBatimentEn;
    private String etageBatimentAr;
    private String equipements;
    private Boolean active;
    private String statut;
}
