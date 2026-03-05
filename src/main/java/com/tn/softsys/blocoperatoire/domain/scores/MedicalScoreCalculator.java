package com.tn.softsys.blocoperatoire.domain.scores;

public interface MedicalScoreCalculator<I, O> {

    O calculate(I input);

    String getVersion();
}