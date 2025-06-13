package com.example.service;

import com.example.dto.HhTokenResponse;
import com.example.model.AuthUser;
import com.example.model.HhToken;
import com.example.model.Resume;
import com.example.model.User;
import com.example.repository.HhTokenRepository;
import com.example.service.common.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HhTokenService {
    private final HhTokenRepository tokenRepository;
    private final ResumeService resumeService;

//    public HhToken findTokenByResumeId(String resumeId) {
//        return tokenRepository.findByResume(resumeId)
//                .orElseThrow(() -> new EntityNotFoundException("HH Token with user ID " + resumeId + " not found"));
//    }

    public HhToken saveToken(HhToken token) {
        return tokenRepository.save(token);
    }

    public HhToken saveTokenFromHh(HhTokenResponse response, User user) {
        Resume resume = resumeService.getResumeByUser(user);
        HhToken hhToken = new HhToken();
        hhToken.setAccessToken(response.getAccessToken());
        hhToken.setRefreshToken(response.getRefreshToken());
        hhToken.setTokenType(response.getTokenType());
        hhToken.setExpiresIn(response.getExpiresIn());
        log.info("Access Token: {}", hhToken.getAccessToken());
        user.setHhToken(hhToken);
        return saveToken(hhToken);
    }

    public boolean checkToken(/*ResumeDto resumeDto, */AuthUser authUser) {
        return isTokenGood(authUser.getUser().getHhToken());
    }

    public boolean isTokenGood(HhToken hhToken) {
        if (hhToken == null
                || hhToken.getAccessToken() == null
                || hhToken.getUpdatedAt() == null
                || hhToken.getExpiresIn() == null) {
            return false;
        }
        // буфер (чтобы не использовать почти истекший токен)
        Duration bufferSeconds = Duration.ofSeconds(60);
        LocalDateTime expirationTime = hhToken.getUpdatedAt()
                .plusSeconds(hhToken.getExpiresIn() - bufferSeconds.toSeconds());

        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expirationTime);
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#section/Avtorizaciya/Obnovlenie-pary-access-i-refresh-tokenov">...</a>
     */
    public boolean refreshTokens() {
        return false;
    }
}
