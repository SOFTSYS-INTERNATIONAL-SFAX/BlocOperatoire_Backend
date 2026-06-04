package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.AlertSettings;
import com.tn.softsys.blocoperatoire.domain.AlertType;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.repository.AlertRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSchedulerService {

    private static final long FIXED_RATE_MS = 10000L;

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

            if (now.isAfter(threshold)) {
                alertRepository
                        .findByTypeAndIntervention_InterventionIdAndActiveTrue(
                                AlertType.SSPI_DEPASSEMENT,
                                sspi.getIntervention().getInterventionId()
                        )
                        .orElseGet(() -> {
                            alertService.createSspiOverrunAlert(sspi);
                            return null;
                        });
            }
        }
    }
}
