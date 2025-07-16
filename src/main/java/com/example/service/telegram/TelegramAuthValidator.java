package com.example.service.telegram;

import com.example.dto.TelegramAuthRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class TelegramAuthValidator {

    public boolean isValid(TelegramAuthRequest req, String botToken) {
        if (req.hash() == null || req.hash().isBlank()) {
            log.warn("Telegram hash is missing in request: {}", req);
            throw new BadCredentialsException("Missing hash in Telegram data");
        }
        if (isAuthOld(req)) {
            return false;
        }
        try {
            Map<String, String> data = new TreeMap<>();
            data.put("auth_date", String.valueOf(req.authDate()));
            data.put("first_name", req.firstName());
            data.put("id", String.valueOf(req.telegramUserId()));
            if (req.lastName() != null) data.put("last_name", req.lastName());
            if (req.photoUrl() != null) data.put("photoUrl", req.photoUrl());
            if (req.username() != null) data.put("username", req.username());

            String dataCheckString = data.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secretKey, "HmacSHA256"));

            byte[] hash = hmac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String actualHash = bytesToHex(hash);

            boolean valid = actualHash.equals(req.hash());
            if (!valid) {
                log.warn("Telegram hash mismatch: expected={}, provided={}, dataString={}", actualHash, req.hash(), dataCheckString);
            }
            return valid;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Telegram auth for username: {} validation failed: {}", req.username(), e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private boolean isAuthOld(TelegramAuthRequest req) {
        // Проверка времени авторизации (защита от replay-атак)
        long currentUnixTime = Instant.now().getEpochSecond();
        long authDateThreshold = Duration.ofMinutes(5L).toSeconds();
        if (Math.abs(currentUnixTime - req.authDate()) > authDateThreshold) {
            log.error("TelegramAuthService: auth_date is too old or in the future. Current: {}, AuthDate: {}", currentUnixTime, req.authDate());
            return true;
        }
        return false;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
