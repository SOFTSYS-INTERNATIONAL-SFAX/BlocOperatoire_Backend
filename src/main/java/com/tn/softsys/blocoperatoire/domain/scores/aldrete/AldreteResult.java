package com.tn.softsys.blocoperatoire.domain.scores.aldrete;

public record AldreteResult(
        int total,
        AldreteDecision decision,
        boolean eligibleForDischarge
) {}