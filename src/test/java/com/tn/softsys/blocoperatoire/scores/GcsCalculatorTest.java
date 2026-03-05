package com.tn.softsys.blocoperatoire.scores;

import com.tn.softsys.blocoperatoire.domain.scores.glasgow.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GcsCalculatorTest {

    private final GcsCalculator calculator = new GcsCalculator();

    @Test
    void shouldCalculateSevereComa() {

        GcsInput input = new GcsInput(
                1,
                1,
                1,
                "test"
        );

        GcsResult result = calculator.calculate(input);

        assertEquals(3, result.total());
        assertEquals(GlasgowGravite.COMA_SEVERE, result.gravite());
    }

    @Test
    void shouldCalculateNormalState() {

        GcsInput input = new GcsInput(
                4,
                5,
                6,
                "test"
        );

        GcsResult result = calculator.calculate(input);

        assertEquals(15, result.total());
        assertEquals(GlasgowGravite.ATTEINTE_LEGERE, result.gravite());
    }
}