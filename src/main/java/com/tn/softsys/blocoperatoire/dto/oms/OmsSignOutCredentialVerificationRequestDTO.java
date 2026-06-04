package com.tn.softsys.blocoperatoire.dto.oms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsSignOutCredentialVerificationRequestDTO {

    private String surgeonPassword;

    private String anesthesistePassword;
}
