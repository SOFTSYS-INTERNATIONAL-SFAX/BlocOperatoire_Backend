package com.tn.softsys.blocoperatoire.domain.scores.apache;

import com.tn.softsys.blocoperatoire.domain.scores.MedicalScoreCalculator;
import org.springframework.stereotype.Component;
@Component
public class ApacheIICalculator implements MedicalScoreCalculator<ApacheInput, ApacheResult> {

    @Override
    public ApacheResult calculate(ApacheInput input) {

        int aps = calculateAPS(input);
        int agePoints = calculateAgePoints(input.getAge());
        int chronicPoints = calculateChronicPoints(input.getChronicHealthStatus());

        int total = aps + agePoints + chronicPoints;

        double r = -3.517 + (0.146 * total);
        double mortality = Math.exp(r) / (1 + Math.exp(r));

        return new ApacheResult(total, mortality);
    }

    private int calculateAPS(ApacheInput i) {
        return scoreTemperature(i.getTemperature())
                + scoreMAP(i.getMap())
                + scoreHeartRate(i.getHeartRate())
                + scoreRespRate(i.getRespiratoryRate())
                + scoreOxygenation(i.getPao2())
                + scorePH(i.getPh())
                + scoreSodium(i.getSodium())
                + scorePotassium(i.getPotassium())
                + scoreCreatinine(i.getCreatinine())
                + scoreHematocrit(i.getHematocrit())
                + scoreWBC(i.getWbc())
                + (15 - i.getGcs());
    }

    // ================= TEMPERATURE =================
    private int scoreTemperature(double t) {
        if (t >= 41) return 4;
        if (t >= 39) return 3;
        if (t >= 38.5) return 1;
        if (t >= 36) return 0;
        if (t >= 34) return 1;
        if (t >= 32) return 2;
        if (t >= 30) return 3;
        return 4;
    }

    // ================= MAP =================
    private int scoreMAP(int map) {
        if (map >= 160) return 4;
        if (map >= 130) return 3;
        if (map >= 110) return 2;
        if (map >= 70) return 0;
        if (map >= 50) return 2;
        return 4;
    }

    // ================= HEART RATE =================
    private int scoreHeartRate(int hr) {
        if (hr >= 180) return 4;
        if (hr >= 140) return 3;
        if (hr >= 110) return 2;
        if (hr >= 70) return 0;
        if (hr >= 55) return 2;
        if (hr >= 40) return 3;
        return 4;
    }

    private int scoreRespRate(int rr) {
        if (rr >= 50) return 4;
        if (rr >= 35) return 3;
        if (rr >= 25) return 1;
        if (rr >= 12) return 0;
        if (rr >= 10) return 1;
        if (rr >= 6) return 2;
        return 4;
    }

    private int scoreOxygenation(int pao2) {
        if (pao2 >= 70) return 0;
        if (pao2 >= 61) return 1;
        if (pao2 >= 55) return 3;
        return 4;
    }

    private int scorePH(double ph) {
        if (ph >= 7.7) return 4;
        if (ph >= 7.6) return 3;
        if (ph >= 7.5) return 1;
        if (ph >= 7.33) return 0;
        if (ph >= 7.25) return 2;
        if (ph >= 7.15) return 3;
        return 4;
    }

    private int scoreSodium(int na) {
        if (na >= 180) return 4;
        if (na >= 160) return 3;
        if (na >= 155) return 2;
        if (na >= 150) return 1;
        if (na >= 130) return 0;
        if (na >= 120) return 2;
        if (na >= 111) return 3;
        return 4;
    }

    private int scorePotassium(double k) {
        if (k >= 7) return 4;
        if (k >= 6) return 3;
        if (k >= 5.5) return 1;
        if (k >= 3.5) return 0;
        if (k >= 3) return 1;
        if (k >= 2.5) return 2;
        return 4;
    }

    private int scoreCreatinine(double cr) {
        if (cr >= 3.5) return 4;
        if (cr >= 2) return 3;
        if (cr >= 1.5) return 2;
        if (cr >= 0.6) return 0;
        return 2;
    }

    private int scoreHematocrit(double hct) {
        if (hct >= 60) return 4;
        if (hct >= 50) return 2;
        if (hct >= 46) return 1;
        if (hct >= 30) return 0;
        if (hct >= 20) return 2;
        return 4;
    }

    private int scoreWBC(double wbc) {
        if (wbc >= 40) return 4;
        if (wbc >= 20) return 2;
        if (wbc >= 15) return 1;
        if (wbc >= 3) return 0;
        if (wbc >= 1) return 2;
        return 4;
    }

    private int calculateAgePoints(int age) {
        if (age <= 44) return 0;
        if (age <= 54) return 2;
        if (age <= 64) return 3;
        if (age <= 74) return 5;
        return 6;
    }

    private int calculateChronicPoints(ChronicHealthStatus status) {
        return switch (status) {
            case NONE -> 0;
            case ELECTIVE_POSTOP -> 2;
            case EMERGENCY_OR_NONOP -> 5;
        };
    }

    @Override
    public String getVersion() {
        return "APACHEII_OFFICIAL_2026_v2";
    }
}