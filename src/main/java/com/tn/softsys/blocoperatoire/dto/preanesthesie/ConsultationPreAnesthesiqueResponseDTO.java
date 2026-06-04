package com.tn.softsys.blocoperatoire.dto.preanesthesie;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class ConsultationPreAnesthesiqueResponseDTO {
    private UUID consultationId;
    private UUID patientId;
    private UUID consentementId;
    private UUID interventionId;
    private UUID anesthesisteId;
    private String anesthesisteNom;
    private String interventionNom;
    private LocalDate interventionDate;
    private LocalTime interventionHeure;
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
    private String medecinNom;
    private UUID validatedByUserId;
    private String validatedByName;
    private LocalDateTime validatedAt;
    private String validationCommentaire;
    private Integer riskScore;
    private String riskLevel;
    private String riskSummary;
    private Long documentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
