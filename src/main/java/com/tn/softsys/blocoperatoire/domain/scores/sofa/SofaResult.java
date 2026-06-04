package com.tn.softsys.blocoperatoire.domain.scores.sofa;

public record SofaResult(
        int respiratoryScore,
        int coagulationScore,
        int liverScore,
        int cardiovascularScore,
        int neurologicalScore,
        int renalScore,
        int totalScore,
        boolean sepsisAlert
) {}