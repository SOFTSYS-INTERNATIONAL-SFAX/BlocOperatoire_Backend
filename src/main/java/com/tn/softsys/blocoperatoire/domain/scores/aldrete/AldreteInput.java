package com.tn.softsys.blocoperatoire.domain.scores.aldrete;

import lombok.*;

@Getter
@AllArgsConstructor
public class AldreteInput {

    private final int activity;       // 0–2
    private final int respiration;    // 0–2
    private final int circulation;    // 0–2
    private final int consciousness;  // 0–2
    private final int oxygenation;    // 0–2
    private final String justification;   // ✅ AJOUT
}