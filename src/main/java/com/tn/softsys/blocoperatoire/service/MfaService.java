package com.tn.softsys.blocoperatoire.service;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

import org.springframework.stereotype.Service;

@Service
public class MfaService {

    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;

    public MfaService() {

        this.secretGenerator = new DefaultSecretGenerator();

        TimeProvider timeProvider = new SystemTimeProvider();
        DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator();

        DefaultCodeVerifier verifier =
                new DefaultCodeVerifier(codeGenerator, timeProvider);

        verifier.setTimePeriod(30);
        verifier.setAllowedTimePeriodDiscrepancy(1); // ±30 secondes

        this.codeVerifier = verifier;
    }

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }

    public String generateQrUrl(String email, String secret) {
        return String.format(
                "otpauth://totp/BlocOperatoire:%s?secret=%s&issuer=BlocOperatoire&algorithm=SHA1&digits=6&period=30",
                email,
                secret
        );
    }
}