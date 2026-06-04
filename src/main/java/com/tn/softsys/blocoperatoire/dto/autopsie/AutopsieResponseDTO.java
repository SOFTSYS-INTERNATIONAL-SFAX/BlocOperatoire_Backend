package com.tn.softsys.blocoperatoire.dto.autopsie;

import com.tn.softsys.blocoperatoire.domain.AutopsieStatut;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AutopsieResponseDTO {

    private UUID autopsieId;
    private UUID decesId;

    private UUID interventionId;
    private UUID patientId;
    private String patientNomComplet;
    private String patientMrn;

    private UUID caseId;
    private String numeroCase;
    private UUID morgueId;
    private String morgueNom;

    private LocalDateTime datePrevue;
    private LocalDateTime dateRealisee;
    private String medecinLegiste;
    private AutopsieStatut statut;
    private String rapport;
    private String observations;
}
