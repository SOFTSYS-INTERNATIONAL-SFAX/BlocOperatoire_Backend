package com.tn.softsys.blocoperatoire.dto.sspi;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SSPIClotureRequestDTO {

    private LocalDateTime heureSortie;
    private String destinationSortie;
    private String motifSortie;
    private Integer aldreteSortie;
    private String decisionMedicale;
    private String observationsSortie;
    private String transmissionResume;
}
