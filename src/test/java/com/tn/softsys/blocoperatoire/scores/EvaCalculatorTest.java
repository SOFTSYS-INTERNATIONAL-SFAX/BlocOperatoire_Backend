package com.tn.softsys.blocoperatoire.scores;

import com.tn.softsys.blocoperatoire.domain.scores.eva.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaCalculatorTest {

    private final EvaCalculator calculator = new EvaCalculator();

    @Test
    void shouldDetectHighPain() {

        EvaInput input = new EvaInput(8, "test");

        EvaResult result = calculator.calculate(input);

        assertEquals(8, result.value());
        assertEquals(EvaAlertLevel.DOULEUR_SEVERE, result.alertLevel());
    }

    @Test
    void shouldDetectLowPain() {

        EvaInput input = new EvaInput(2, "test");

        EvaResult result = calculator.calculate(input);

        assertEquals(2, result.value());
        assertEquals(EvaAlertLevel.NORMAL, result.alertLevel());
    }
}