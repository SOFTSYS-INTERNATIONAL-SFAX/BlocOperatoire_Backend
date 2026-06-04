package com.tn.softsys.blocoperatoire.domain.scores.eva;

import lombok.*;

@Getter
@AllArgsConstructor
public class EvaInput {

    private final int value; // 0–10
    private final String justification;   // ✅ AJOUT

}