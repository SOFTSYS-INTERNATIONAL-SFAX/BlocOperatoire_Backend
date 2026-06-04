package com.tn.softsys.blocoperatoire.domain.scores.asa;

public record AsaResult(
        AsaClass asaClass,
        boolean emergency,
        String finalClassification
) {}