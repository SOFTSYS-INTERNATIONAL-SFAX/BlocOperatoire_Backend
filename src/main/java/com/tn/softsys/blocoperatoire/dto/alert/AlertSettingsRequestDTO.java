package com.tn.softsys.blocoperatoire.dto.alert;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertSettingsRequestDTO {

    @NotNull
    @Min(1)
    private Integer sspiThresholdMinutes;

    @NotNull
    private Boolean soundEnabled;

    @NotNull
    @Min(1)
    private Integer escalationLevel1Minutes;

    @NotNull
    @Min(1)
    private Integer escalationLevel2Minutes;
}
