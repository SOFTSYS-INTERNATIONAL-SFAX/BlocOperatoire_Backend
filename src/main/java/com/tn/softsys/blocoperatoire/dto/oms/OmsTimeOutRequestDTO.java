package com.tn.softsys.blocoperatoire.dto.oms;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OmsTimeOutRequestDTO {

    @NotNull
    private Boolean teamIntroduced;

    @NotNull
    private Boolean patientNameConfirmed;

    @NotNull
    private Boolean interventionConfirmed;

    @NotNull
    private Boolean siteConfirmed;

    private Boolean antibioticProphylaxisGiven;
    private Boolean imagingDisplayed;

    // Optionnels
    private String criticalEventsSurgeon;
    private String criticalEventsAnesthesia;
}
