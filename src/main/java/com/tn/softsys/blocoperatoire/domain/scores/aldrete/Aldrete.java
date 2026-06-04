package com.tn.softsys.blocoperatoire.domain.scores.aldrete;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "score_aldrete")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aldrete extends Score {

    @Column(nullable = false)
    private int activity;

    @Column(nullable = false)
    private int respiration;

    @Column(nullable = false)
    private int circulation;

    @Column(nullable = false)
    private int consciousness;

    @Column(nullable = false)
    private int oxygenation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AldreteDecision decision;

    public void initType() {
        setScoreType(ScoreType.ALDRETE);
    }
}