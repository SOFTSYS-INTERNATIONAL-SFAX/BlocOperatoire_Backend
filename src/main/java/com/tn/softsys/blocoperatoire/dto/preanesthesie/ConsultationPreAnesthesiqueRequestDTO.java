package com.tn.softsys.blocoperatoire.dto.preanesthesie;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConsultationPreAnesthesiqueRequestDTO {

    @NotNull
    private UUID patientId;

    private UUID consentementId;
    private UUID interventionId;
    private UUID anesthesisteId;
    private UUID asaId;
    private String asaCode;
    private Boolean urgence;
    private Double poidsKg;
    private Double tailleCm;
    private Integer paSystolique;
    private Integer paDiastolique;
    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Integer spo2;
    private Integer temperatureDixieme;
    private String mallampatiCode;
    private Integer ouvertureBucaleMm;
    private String mobiliteCervicale;
    private String typeAnesthesie;
    private String considerations;
    private String allergiesResume;
    private String traitementsChroniques;
    private String antecedentsMedicauxResume;
    private String antecedentsAnesthesiques;
    private String evaluationCardioRespiratoire;
    private String examensComplementairesResume;
    private String etatDentaire;
    private String risqueHemorragique;
    private String strategieAnesthesique;
    private Boolean jeuneConfirme;
    private Integer jeuneHeures;
    private Boolean consentementEclaireObtenu;
    private Boolean voieAerienneDifficileSuspectee;
    private String notesComplementaires;
    private Boolean validee;
    private String validationCommentaire;
}
