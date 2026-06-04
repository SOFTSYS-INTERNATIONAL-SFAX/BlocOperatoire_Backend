package com.tn.softsys.blocoperatoire.domain.scores.aldrete;

import com.tn.softsys.blocoperatoire.domain.scores.MedicalScoreCalculator;
import org.springframework.stereotype.Component;

@Component
public class AldreteCalculator implements MedicalScoreCalculator<AldreteInput, AldreteResult> {

    @Override
    public AldreteResult calculate(AldreteInput input) {

        validate(input);

        int total = input.getActivity()
                + input.getRespiration()
                + input.getCirculation()
                + input.getConsciousness()
                + input.getOxygenation();

        AldreteDecision decision;
        boolean eligible;

        if (total >= 9) {
            decision = AldreteDecision.AUTORISATION_SORTIE_SSPI;
            eligible = true;
        } else {
            decision = AldreteDecision.POURSUITE_SURVEILLANCE;
            eligible = false;
        }

        return new AldreteResult(total, decision, eligible);
    }

    private void validate(AldreteInput input) {

        validateRange(input.getActivity(), "Activity");
        validateRange(input.getRespiration(), "Respiration");
        validateRange(input.getCirculation(), "Circulation");
        validateRange(input.getConsciousness(), "Consciousness");
        validateRange(input.getOxygenation(), "Oxygenation");
    }

    private void validateRange(int value, String field) {
        if (value < 0 || value > 2) {
            throw new IllegalArgumentException(field + " must be between 0 and 2");
        }
    }

    @Override
    public String getVersion() {
        return "ALDRETE_2026_v1";
    }
}