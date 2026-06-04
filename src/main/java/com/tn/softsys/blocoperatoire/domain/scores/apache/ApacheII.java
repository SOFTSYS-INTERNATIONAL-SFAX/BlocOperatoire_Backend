package com.tn.softsys.blocoperatoire.domain.scores.apache;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "score_apache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApacheII extends Score {

    private double temperature;
    private int map;
    private int heartRate;
    private int respiratoryRate;
    private int pao2;
    private double ph;
    private int sodium;
    private double potassium;
    private double creatinine;
    private double hematocrit;
    private double wbc;
    private int gcs;
    private int age;

    @Enumerated(EnumType.STRING)
    private ChronicHealthStatus chronicHealthStatus;

    private double mortalityProbability;
}