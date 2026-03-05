package com.tn.softsys.blocoperatoire.domain.scores.sofa;

import lombok.*;

@Getter
@AllArgsConstructor

public class SofaInput {

    private final double pao2;
    private final double fio2;
    private final String justification;   // 3ème paramètre
    private final boolean mechanicalVentilation;

    private final int platelets;
    private final double bilirubin;

    private final double map;
    private final Double dopamine;
    private final Double dobutamine;
    private final Double epinephrine;
    private final Double norepinephrine;

    private final int gcs;

    private final double creatinine;
    private final Double urineOutput24h;
}