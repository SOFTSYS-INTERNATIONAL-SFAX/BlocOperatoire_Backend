package com.tn.softsys.blocoperatoire.dto.alert;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AlertSettingsResponseDTO {

    private UUID settingsId;
    private Integer sspiThresholdMinutes;
    private Boolean soundEnabled;
    private Integer escalationLevel1Minutes;
    private Integer escalationLevel2Minutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
