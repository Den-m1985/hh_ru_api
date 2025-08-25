package com.example.service.superjob;

import com.example.model.SuperjobToken;
import com.example.model.User;
import com.example.repository.SuperjobTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperjobTokenService {
    private final SuperjobTokenRepository superjobTokenRepository;

    public void saveToken(SuperjobToken newToken) {
        superjobTokenRepository.save(newToken);
    }

    public SuperjobToken getTokenFromDb(User user) {
        return superjobTokenRepository.findSuperjobTokenByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("SuperjobToken with user_id: " + user.getId() + " not found"));
    }

    public boolean isTokenGood(SuperjobToken superjobToken) {
        if (superjobToken == null
                || superjobToken.getAccessToken() == null
                || superjobToken.getUpdatedAt() == null
                || superjobToken.getExpiresIn() == null) {
            return false;
        }
        LocalDateTime expirationTime = superjobToken.getUpdatedAt().plusSeconds(superjobToken.getExpiresIn());
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expirationTime);
    }
}
