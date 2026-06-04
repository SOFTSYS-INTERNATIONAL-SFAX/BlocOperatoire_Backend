package com.tn.softsys.blocoperatoire.domain.scores.asa;

import com.tn.softsys.blocoperatoire.domain.scores.MedicalScoreCalculator;
import org.springframework.stereotype.Component;

@Component
public class AsaCalculator implements MedicalScoreCalculator<AsaInput, AsaResult> {

    @Override
    public AsaResult calculate(AsaInput input) {

        AsaClass asaClass;

        if (input.isBrainDeadDonor()) {
            asaClass = AsaClass.ASA_VI;
        } else if (input.isMoribund()) {
            asaClass = AsaClass.ASA_V;
        } else if (input.isLifeThreateningDisease()) {
            asaClass = AsaClass.ASA_IV;
        } else if (input.isSevereSystemicDisease()) {
            asaClass = AsaClass.ASA_III;
        } else if (input.isMildSystemicDisease()) {
            asaClass = AsaClass.ASA_II;
        } else {
            asaClass = AsaClass.ASA_I;
        }

        String finalClassification = asaClass.name();

        if (input.isEmergency()) {
            finalClassification += "_E";
        }

        return new AsaResult(
                asaClass,
                input.isEmergency(),
                finalClassification
        );
    }

    @Override
    public String getVersion() {
        return "ASA_2026_v1";
    }
}