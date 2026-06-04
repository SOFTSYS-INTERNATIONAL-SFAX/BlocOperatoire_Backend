package com.tn.softsys.blocoperatoire.dto.interventioncatalog;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class InterventionCatalogResponseDTO {

    private UUID catalogId;
    private String designation;
    private String designationEn;
    private String designationAr;
    private String cotationUnite;
    private BigDecimal cotationValeur;
    private Integer dureeMinutes;
}
