package com.tn.softsys.blocoperatoire.scores;

import com.tn.softsys.blocoperatoire.domain.scores.apache.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApacheCalculatorTest {

    private final ApacheIICalculator calculator = new ApacheIICalculator();

    @Test
    void shouldAddAgePoints() {

        ApacheInput input = ApacheInput.builder()
                .temperature(37)
                .map(80)
                .heartRate(70)
                .respiratoryRate(16)
                .pao2(90)
                .ph(7.4)
                .sodium(140)
                .potassium(4)
                .creatinine(1)
                .hematocrit(40)
                .wbc(8)
                .gcs(15)
                .age(80)
                .justification("test")
                .chronicHealthStatus(ChronicHealthStatus.NONE)
                .build();

        ApacheResult result = calculator.calculate(input);

        assertNotNull(result);
        assertTrue(result.totalScore() >= 6);
    }
}