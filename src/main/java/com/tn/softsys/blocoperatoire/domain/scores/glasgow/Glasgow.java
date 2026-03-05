package com.tn.softsys.blocoperatoire.domain.scores.glasgow;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "score_glasgow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Glasgow extends Score {

    @Column(nullable = false)
    private Integer eyes;

    @Column(nullable = false)
    private Integer verbal;

    @Column(nullable = false)
    private Integer moteur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlasgowGravite gravite;

    @PrePersist
    public void initType() {
        setScoreType(ScoreType.GCS);
    }
}