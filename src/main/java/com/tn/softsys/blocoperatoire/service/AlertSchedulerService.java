package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Alert;
import com.tn.softsys.blocoperatoire.domain.AlertSettings;
import com.tn.softsys.blocoperatoire.domain.AlertType;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.repository.AlertRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSchedulerService {

    private static final long FIXED_RATE_MS = 10000L;
    private static final long COOLDOWN_AFTER_ACK_MINUTES = 15L;

    private final SSPIRepository sspiRepository;
    private final AlertRepository alertRepository;
    private final AlertService alertService;
    private final AlertSettingsService alertSettingsService;

    @Scheduled(fixedRate = FIXED_RATE_MS)
    public void checkSspiOverruns() {
        List<SSPI> activeSspi = sspiRepository.findByHeureSortieIsNull();
        LocalDateTime now = LocalDateTime.now();
        AlertSettings settings = alertSettingsService.getSettingsEntity();
        long thresholdMinutes = settings.getSspiThresholdMinutes();

        for (SSPI sspi : activeSspi) {
            if (sspi.getHeureEntree() == null
                    || sspi.getIntervention() == null
                    || sspi.getIntervention().getInterventionId() == null) {
                continue;
            }

            LocalDateTime threshold = sspi.getHeureEntree().plusMinutes(thresholdMinutes);

            if (!now.isAfter(threshold)) {
                continue;
            }

            UUID interventionId = sspi.getIntervention().getInterventionId();

            // Skip if an active alert already exists
            Optional<Alert> existing = alertRepository
                    .findByTypeAndIntervention_InterventionIdAndActiveTrue(
                            AlertType.SSPI_DEPASSEMENT, interventionId
                    );

            if (existing.isPresent()) {
                continue;
            }

            // Don't recreate within cooldown period after the user acknowledged
            Optional<Alert> lastAcked = alertRepository
                    .findTopByTypeAndIntervention_InterventionIdAndAcknowledgedTrueOrderByAcknowledgedAtDesc(
                            AlertType.SSPI_DEPASSEMENT, interventionId
                    );

            if (lastAcked.isPresent() && lastAcked.get().getAcknowledgedAt() != null) {
                long minutesSinceAck = Duration.between(lastAcked.get().getAcknowledgedAt(), now).toMinutes();
                if (minutesSinceAck < COOLDOWN_AFTER_ACK_MINUTES) {
                    continue;
                }
            }

            alertService.createSspiOverrunAlert(sspi);
        }
    }
}
