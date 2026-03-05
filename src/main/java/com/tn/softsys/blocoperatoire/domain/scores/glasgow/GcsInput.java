package com.tn.softsys.blocoperatoire.domain.scores.glasgow;

import lombok.*;

@Getter
@AllArgsConstructor
public class GcsInput {

    private final int eyes;
    private final int verbal;

    private final int motor;
    private final String justification;   // ✅ AJOUT

}