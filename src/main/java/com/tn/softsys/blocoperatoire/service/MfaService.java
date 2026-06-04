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
    private final DefaultCodeGenerator codeGenerator;
    private final TimeProvider timeProvider;

    public MfaService() {

        this.secretGenerator = new DefaultSecretGenerator();

        this.timeProvider = new SystemTimeProvider();
        this.codeGenerator = new DefaultCodeGenerator();

        DefaultCodeVerifier verifier =
                new DefaultCodeVerifier(codeGenerator, timeProvider);

        // Standard RFC 6238
        verifier.setTimePeriod(30);

        // Tolérance ±30 secondes
        verifier.setAllowedTimePeriodDiscrepancy(1);

        this.codeVerifier = verifier;
    }

    /* ================= SECRET ================= */

    public String generateSecret() {
        return secretGenerator.generate();
    }

    /* ================= VERIFY ================= */

    public boolean verifyCode(String secret, String code) {

        if (secret == null || code == null) {
            return false;
        }

        String normalizedSecret = normalizeSecret(secret);
        String normalizedCode = code.trim();

        try {
            return codeVerifier.isValidCode(normalizedSecret, normalizedCode);
        } catch (Exception e) {
            return false;
        }
    }

    /* ================= QR ================= */

    public String generateQrUrl(String email, String secret) {

        String normalizedSecret = normalizeSecret(secret);

        return String.format(
                "otpauth://totp/BlocOperatoire:%s?secret=%s&issuer=BlocOperatoire&algorithm=SHA1&digits=6&period=30",
                email,
                normalizedSecret
        );
    }

    /* ================= DEBUG ================= */

    public String generateCurrentCode(String secret) {

        if (secret == null) {
            return "SECRET_NULL";
        }

        String normalizedSecret = normalizeSecret(secret);

        try {
            return codeGenerator.generate(
                    normalizedSecret,
                    timeProvider.getTime()
            );
        } catch (Exception e) {
            return "INVALID_SECRET";
        }
    }

    /* ================= NORMALIZE ================= */

    private String normalizeSecret(String secret) {

        return secret
                .trim()
                .replace(" ", "")
                .toUpperCase();
    }
}