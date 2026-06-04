package com.tn.softsys.blocoperatoire.domain.scores.eva;

import com.tn.softsys.blocoperatoire.domain.scores.MedicalScoreCalculator;
import org.springframework.stereotype.Component;

@Component
public class EvaCalculator implements MedicalScoreCalculator<EvaInput, EvaResult> {

    @Override
    public EvaResult calculate(EvaInput input) {

        validate(input);

        int value = input.getValue();

        EvaAlertLevel level;
        boolean severeAlert = false;

        if (value >= 7) {
            level = EvaAlertLevel.DOULEUR_SEVERE;
            severeAlert = true;
        } else if (value >= 4) {
            level = EvaAlertLevel.DOULEUR_MODEREE;
        } else {
            level = EvaAlertLevel.NORMAL;
        }

        return new EvaResult(value, level, severeAlert);
    }

    private void validate(EvaInput input) {

        if (input.getValue() < 0 || input.getValue() > 10) {
            throw new IllegalArgumentException("EVA must be between 0 and 10");
        }
    }

    @Override
    public String getVersion() {
        return "EVA_2026_v1";
    }
}