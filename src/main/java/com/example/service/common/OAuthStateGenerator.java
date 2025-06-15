package com.example.service.common;

import com.example.dto.StatePayload;
import com.example.enums.Device;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthStateGenerator {
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;

    public String generateEncryptedState(Integer userId, Device device) {
        try {
            StatePayload payload = new StatePayload(userId, device);
            String json = objectMapper.writeValueAsString(payload);
            return cryptoService.encrypt(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public StatePayload parseState(String encryptedState) {
        try {
            String json = cryptoService.decrypt(encryptedState);
            return objectMapper.readValue(json, StatePayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
