package com.example.service.telegram;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TelegramLinkService {
    private Cache<String, Integer> codeToUserIdCache;

    @PostConstruct
    public void init() {
        // Генерация кэша для 10 минут жизни кода
        codeToUserIdCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    public String generateLinkCode(Integer userId) {
        String code = UUID.randomUUID().toString().substring(0, 8);
        codeToUserIdCache.put(code, userId);
        return code;
    }

    public Optional<Integer> getUserIdByCode(String code) {
        return Optional.ofNullable(codeToUserIdCache.getIfPresent(code));
    }
}
