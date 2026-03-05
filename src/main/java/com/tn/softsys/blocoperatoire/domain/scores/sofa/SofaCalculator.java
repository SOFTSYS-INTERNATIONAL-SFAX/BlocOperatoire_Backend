package com.tn.softsys.blocoperatoire.domain.scores.sofa;

import com.tn.softsys.blocoperatoire.domain.scores.MedicalScoreCalculator;
import org.springframework.stereotype.Component;

@Component
public class SofaCalculator implements MedicalScoreCalculator<SofaInput, SofaResult> {

    @Override
    public SofaResult calculate(SofaInput input) {

        validate(input);

        int resp = respiratory(input);
        int coag = coagulation(input);
        int liver = liver(input);
        int cardio = cardiovascular(input);
        int neuro = neurological(input);
        int renal = renal(input);

        int total = resp + coag + liver + cardio + neuro + renal;

        return new SofaResult(
                resp,
                coag,
                liver,
                cardio,
                neuro,
                renal,
                total,
                false // delta géré au niveau service
        );
    }

    // ================= VALIDATION =================

    private void validate(SofaInput i) {
        if (i.getFio2() <= 0 || i.getFio2() > 1.0)
            throw new IllegalArgumentException("FiO2 must be between 0 and 1");

        if (i.getPao2() <= 0)
            throw new IllegalArgumentException("PaO2 must be positive");

        if (i.getPlatelets() < 0)
            throw new IllegalArgumentException("Platelets must be positive");

        if (i.getGcs() < 3 || i.getGcs() > 15)
            throw new IllegalArgumentException("GCS must be between 3 and 15");
    }

    // ================= RESPIRATORY =================

    private int respiratory(SofaInput i) {

        double ratio = i.getPao2() / i.getFio2();

        if (ratio >= 400) return 0;
        if (ratio >= 300) return 1;
        if (ratio >= 200) return 2;

        if (ratio < 200 && !i.isMechanicalVentilation())
            return 2;

        if (ratio >= 100 && i.isMechanicalVentilation())
            return 3;

        if (ratio < 100 && i.isMechanicalVentilation())
            return 4;

        return 0;
    }

    // ================= COAGULATION =================

    private int coagulation(SofaInput i) {
        int p = i.getPlatelets();

        if (p >= 150) return 0;
        if (p >= 100) return 1;
        if (p >= 50) return 2;
        if (p >= 20) return 3;
        return 4;
    }

    // ================= LIVER =================

    private int liver(SofaInput i) {
        double b = i.getBilirubin();

        if (b < 1.2) return 0;
        if (b < 2.0) return 1;
        if (b < 6.0) return 2;
        if (b < 12.0) return 3;
        return 4;
    }

    // ================= CARDIOVASCULAR =================

    private int cardiovascular(SofaInput i) {

        if (i.getDopamine() != null && i.getDopamine() > 15) return 4;
        if (i.getEpinephrine() != null && i.getEpinephrine() > 0.1) return 4;
        if (i.getNorepinephrine() != null && i.getNorepinephrine() > 0.1) return 4;

        if (i.getDopamine() != null && i.getDopamine() > 5) return 3;
        if (i.getEpinephrine() != null && i.getEpinephrine() <= 0.1) return 3;
        if (i.getNorepinephrine() != null && i.getNorepinephrine() <= 0.1) return 3;

        if (i.getDopamine() != null && i.getDopamine() <= 5) return 2;
        if (i.getDobutamine() != null) return 2;

        if (i.getMap() < 70) return 1;

        return 0;
    }

    // ================= NEUROLOGICAL =================

    private int neurological(SofaInput i) {

        int gcs = i.getGcs();

        if (gcs == 15) return 0;
        if (gcs >= 13) return 1;
        if (gcs >= 10) return 2;
        if (gcs >= 6) return 3;
        return 4;
    }

    // ================= RENAL =================

    private int renal(SofaInput i) {

        if (i.getCreatinine() >= 5
                || (i.getUrineOutput24h() != null && i.getUrineOutput24h() < 200))
            return 4;

        if (i.getCreatinine() >= 3.5
                || (i.getUrineOutput24h() != null && i.getUrineOutput24h() < 500))
            return 3;

        if (i.getCreatinine() >= 2.0) return 2;

        if (i.getCreatinine() >= 1.2) return 1;

        return 0;
    }

    @Override
    public String getVersion() {
        return "SOFA_ICU_OFFICIAL_2026_v2";
    }
}