package com.tn.softsys.blocoperatoire.domain.scores.asa;

import lombok.*;

@Getter
@AllArgsConstructor
public class AsaInput {

    private final boolean brainDeadDonor;
    private final boolean moribund;
    private final boolean lifeThreateningDisease;
    private final boolean severeSystemicDisease;
    private final boolean mildSystemicDisease;
    private final boolean emergency;
    private final String justification;   // ✅ AJOUT

}