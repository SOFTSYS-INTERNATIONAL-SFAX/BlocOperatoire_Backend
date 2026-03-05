package com.tn.softsys.blocoperatoire.scores;

import com.tn.softsys.blocoperatoire.domain.scores.sofa.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SofaCalculatorTest {

    private final SofaCalculator calculator = new SofaCalculator();

    @Test
    void shouldCalculateSofaScore() {

        SofaInput input = new SofaInput(
                80,          // paO2
                0.5,         // fiO2
                "test",      // justification
                true,        // ventilation
                40,          // platelets
                3.5,         // bilirubin
                60,          // MAP
                6.0,         // dopamine
                null,        // dobutamine
                null,        // epinephrine
                null,        // norepinephrine
                7,           // gcs
                2.5,         // creatinine
                500.0        // urineOutput
        );

        SofaResult result = calculator.calculate(input);

        assertNotNull(result);
        assertTrue(result.totalScore() > 0);
    }
}