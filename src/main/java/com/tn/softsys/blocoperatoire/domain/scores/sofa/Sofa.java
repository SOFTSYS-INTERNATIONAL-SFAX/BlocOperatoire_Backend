package com.tn.softsys.blocoperatoire.domain.scores.sofa;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "score_sofa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sofa extends Score {

    private int respiratoryScore;
    private int coagulationScore;
    private int liverScore;
    private int cardiovascularScore;
    private int neurologicalScore;
    private int renalScore;
    @Column(nullable = false)
    private boolean sepsisAlert;
    public void initType() {
        setScoreType(ScoreType.SOFA);
    }
}