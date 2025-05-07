package com.example.service;

import com.example.model.HhToken;
import com.example.repository.HhTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final HhTokenRepository tokenRepository;

    public HhToken findTokenByUserId(String userId) {
        return tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("HH Token with user ID " + userId + " not found"));
    }

    public HhToken saveToken(HhToken token) {
        return tokenRepository.save(token);
    }
}
