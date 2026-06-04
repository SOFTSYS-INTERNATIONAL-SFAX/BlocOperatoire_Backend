package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.AlertSettings;
import com.tn.softsys.blocoperatoire.dto.alert.AlertSettingsRequestDTO;
import com.tn.softsys.blocoperatoire.dto.alert.AlertSettingsResponseDTO;
import com.tn.softsys.blocoperatoire.repository.AlertSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSettingsService {

    private final AlertSettingsRepository repository;

    @Transactional(readOnly = true)
    public AlertSettingsResponseDTO getSettings() {
        return toDTO(getOrCreateEntity());
    }

    @Transactional(readOnly = true)
    public AlertSettings getSettingsEntity() {
        return getOrCreateEntity();
    }

    public AlertSettingsResponseDTO updateSettings(AlertSettingsRequestDTO dto) {
        validate(dto);

        AlertSettings entity = getOrCreateEntity();

        entity.setSspiThresholdMinutes(dto.getSspiThresholdMinutes());
        entity.setSoundEnabled(dto.getSoundEnabled());
        entity.setEscalationLevel1Minutes(dto.getEscalationLevel1Minutes());
        entity.setEscalationLevel2Minutes(dto.getEscalationLevel2Minutes());

        return toDTO(repository.save(entity));
    }

    private AlertSettings getOrCreateEntity() {
        return repository.findTopByOrderByCreatedAtAsc()
                .orElseGet(() -> repository.save(AlertSettings.builder().build()));
    }

    private void validate(AlertSettingsRequestDTO dto) {
        if (dto.getEscalationLevel1Minutes() <= dto.getSspiThresholdMinutes()) {
            throw new IllegalArgumentException("Escalation level 1 must be greater than SSPI threshold");
        }

        if (dto.getEscalationLevel2Minutes() <= dto.getEscalationLevel1Minutes()) {
            throw new IllegalArgumentException("Escalation level 2 must be greater than escalation level 1");
        }
    }

    private AlertSettingsResponseDTO toDTO(AlertSettings entity) {
        return AlertSettingsResponseDTO.builder()
                .settingsId(entity.getSettingsId())
                .sspiThresholdMinutes(entity.getSspiThresholdMinutes())
                .soundEnabled(entity.getSoundEnabled())
                .escalationLevel1Minutes(entity.getEscalationLevel1Minutes())
                .escalationLevel2Minutes(entity.getEscalationLevel2Minutes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
