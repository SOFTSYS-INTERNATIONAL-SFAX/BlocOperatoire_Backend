package com.tn.softsys.blocoperatoire.domain.scores.eva;

public record EvaResult(
        int value,
        EvaAlertLevel alertLevel,
        boolean severeAlert
) {}