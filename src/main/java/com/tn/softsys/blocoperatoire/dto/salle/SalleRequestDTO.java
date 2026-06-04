package com.tn.softsys.blocoperatoire.dto.salle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalleRequestDTO {

    @NotBlank
    private String nom;

    private String nomEn;
    private String nomAr;

    @NotBlank
    private String idBloc;

    private String idBlocEn;
    private String idBlocAr;

    @NotBlank
    private String etageBatiment;

    private String etageBatimentEn;
    private String etageBatimentAr;

    private String equipements;

    @NotNull
    private Boolean active;

    private String statut;
}
