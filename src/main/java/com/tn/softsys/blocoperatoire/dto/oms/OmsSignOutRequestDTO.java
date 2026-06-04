package com.tn.softsys.blocoperatoire.dto.oms;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OmsSignOutRequestDTO {

    @NotNull
    private Boolean interventionRecorded;

    @NotNull
    private Boolean instrumentsCountCorrect;

    @NotNull
    private Boolean specimensLabeled;

    @NotNull
    private Boolean recoveryPlanConfirmed;

    @NotNull
    private Boolean surgeonValidated;

    @NotNull
    private Boolean anesthesisteValidated;

    @NotBlank
    private String surgeonPassword;

    @NotBlank
    private String anesthesistePassword;

    private String equipmentProblems;
}
