package com.example.service;

import com.example.dto.StatePayload;
import com.example.enums.ApiProvider;
import com.example.service.common.OAuthStateGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallBackService {
    private final OAuthClient oauthClient;
    private final OAuthStateGenerator oAuthStateGenerator;

    public void authenticate(ApiProvider provider, String code, String state) {
        StatePayload statePayload = oAuthStateGenerator.parseState(state);
        switch (provider) {
            case SUPERJOB -> oauthClient.authenticateSuperJobUser(code, statePayload);
            case HEADHUNTER -> oauthClient.authenticateHeadHunter(code, statePayload);
            default -> {
                log.error("Unsupported OAuth provider: {}", provider);
                throw new IllegalArgumentException("Unsupported provider: " + provider);
            }
        }
        log.info("Finished oauth with user id:{}", statePayload.userId());
    }

}
