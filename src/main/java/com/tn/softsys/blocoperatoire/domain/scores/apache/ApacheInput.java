package com.tn.softsys.blocoperatoire.domain.scores.apache;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor

public class ApacheInput {

    private final double temperature;
    private final int map;
    private final int heartRate;
    private final int respiratoryRate;
    private final int pao2;
    private final double ph;
    private final int sodium;
    private final double potassium;
    private final double creatinine;
    private final double hematocrit;
    private final double wbc;
    private final int gcs;
    private final int age;
    private final String justification;   // ✅ AJOUT


    private final ChronicHealthStatus chronicHealthStatus;
}