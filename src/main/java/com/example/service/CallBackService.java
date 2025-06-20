package com.example.service;

import com.example.dto.StatePayload;
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

    public void processApp(String code, String state) {
        log.debug("Получен код: {} и state: {}", code, state);

        StatePayload statePayload = oAuthStateGenerator.parseState(state);

        oauthClient.authenticate(code, statePayload);

        log.info("Finished oauth with user id:{}", statePayload.userId());
    }
}
