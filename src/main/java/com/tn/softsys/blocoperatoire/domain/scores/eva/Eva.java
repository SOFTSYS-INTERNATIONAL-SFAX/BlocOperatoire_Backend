package com.tn.softsys.blocoperatoire.domain.scores.eva;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "score_eva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Eva extends Score {

    @Column(nullable = false)
    private int value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaAlertLevel alertLevel;

    @PrePersist
    public void initType() {
        setScoreType(ScoreType.EVA);
    }
}