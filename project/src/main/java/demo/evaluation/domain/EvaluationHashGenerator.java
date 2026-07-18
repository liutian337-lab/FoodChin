package demo.evaluation.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EvaluationHashGenerator {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private EvaluationHashGenerator() { }

    public static String generate(Long foodId, BigDecimal score, String level, BigDecimal confidence,
                                  String modelVersion, LocalDateTime evaluationTime) {
        String payload = foodId + "|" + decimal(score, 2) + "|" + level.trim() + "|" + decimal(confidence, 4)
                + "|" + modelVersion.trim() + "|" + TIME_FORMATTER.format(evaluationTime);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(64);
            for (byte value : digest) result.append(String.format("%02x", value & 0xff));
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static String decimal(BigDecimal value, int scale) {
        return value == null ? "" : value.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    }
}
