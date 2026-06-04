package com.tn.softsys.blocoperatoire.dto.oms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmsSignOutCredentialVerificationResponseDTO {

    private boolean surgeonVerified;
    private boolean anesthesisteVerified;
    private String surgeonMessage;
    private String anesthesisteMessage;
    private String surgeonLabel;
    private String anesthesisteLabel;
}
