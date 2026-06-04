package com.tn.softsys.blocoperatoire.domain.scores.apache;

public record ApacheResult(
        int totalScore,
        double mortalityProbability
) {}