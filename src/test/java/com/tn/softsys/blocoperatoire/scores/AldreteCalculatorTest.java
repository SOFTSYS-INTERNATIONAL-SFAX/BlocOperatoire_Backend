package com.tn.softsys.blocoperatoire.scores;

import com.tn.softsys.blocoperatoire.domain.scores.aldrete.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AldreteCalculatorTest {

    private final AldreteCalculator calculator = new AldreteCalculator();

    @Test
    void shouldAllowDischarge() {

        AldreteInput input = new AldreteInput(
                2,
                2,
                2,
                2,
                2,
                "test"
        );

        AldreteResult result = calculator.calculate(input);

        assertEquals(AldreteDecision.AUTORISATION_SORTIE_SSPI, result.decision());
    }

    @Test
    void shouldNotAllowDischarge() {

        AldreteInput input = new AldreteInput(
                1,
                1,
                1,
                1,
                1,
                "test"
        );

        AldreteResult result = calculator.calculate(input);

        assertEquals(AldreteDecision.POURSUITE_SURVEILLANCE, result.decision());
    }
}