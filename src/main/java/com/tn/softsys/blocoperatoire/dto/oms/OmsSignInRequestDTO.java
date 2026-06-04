package com.tn.softsys.blocoperatoire.dto.oms;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OmsSignInRequestDTO {

    @NotNull
    private Boolean patientIdentityConfirmed;

    @NotNull
    private Boolean siteMarked;

    @NotNull
    private Boolean anesthesiaMachineChecked;

    @NotNull
    private Boolean pulseOximeterWorking;

    private Boolean difficultAirwayRisk;
    private Boolean aspirationRisk;
    private Boolean hemorrhageRisk;
    private Boolean bloodProductsAvailable;
    private String allergies;
}
