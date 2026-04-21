package tr.duzce.edu.mf.bm.KurumArizaTakip.service.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class VerificationCodeHasher {

    private VerificationCodeHasher() {
    }

    public static String sha256(String code) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Verification code hashing failed", e);
        }
    }
}

