package com.tn.softsys.blocoperatoire.domain.scores.glasgow;

import com.tn.softsys.blocoperatoire.domain.scores.MedicalScoreCalculator;
import org.springframework.stereotype.Component;

@Component
public class GcsCalculator implements MedicalScoreCalculator<GcsInput, GcsResult> {

    @Override
    public GcsResult calculate(GcsInput input) {

        validate(input);

        int total = input.getEyes()
                + input.getVerbal()
                + input.getMotor();

        GlasgowGravite gravite;

        if (total <= 8) {
            gravite = GlasgowGravite.COMA_SEVERE;
        } else if (total <= 12) {
            gravite = GlasgowGravite.ATTEINTE_MODEREE;
        } else {
            gravite = GlasgowGravite.ATTEINTE_LEGERE;
        }

        return new GcsResult(total, gravite);
    }

    private void validate(GcsInput input) {

        if (input.getEyes() < 1 || input.getEyes() > 4)
            throw new IllegalArgumentException("Eyes must be between 1 and 4");

        if (input.getVerbal() < 1 || input.getVerbal() > 5)
            throw new IllegalArgumentException("Verbal must be between 1 and 5");

        if (input.getMotor() < 1 || input.getMotor() > 6)
            throw new IllegalArgumentException("Motor must be between 1 and 6");
    }

    @Override
    public String getVersion() {
        return "GCS_2026_v1";
    }
}