package com.example.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrowserOpenerRunner implements CommandLineRunner {
    private final OAuthClient oauthClient;

    @Override
    public void run(String... args) {
        String url = oauthClient.getAuthorizeUrl();
        BrowserOpener.openInBrowser(url);
    }
}
