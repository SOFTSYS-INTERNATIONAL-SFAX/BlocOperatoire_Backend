package com.tn.softsys.blocoperatoire.domain.scores.asa;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "score_asa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asa extends Score {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AsaClass asaClass;

    @Column(nullable = false)
    private boolean emergency;

    @Column(nullable = false)
    private String finalClassification;

    public void initType() {
        setScoreType(ScoreType.ASA);
    }
}