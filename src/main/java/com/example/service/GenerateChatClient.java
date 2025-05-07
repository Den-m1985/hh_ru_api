package com.example.service;

import org.springframework.stereotype.Component;

@Component
public class GenerateChatClient {

    public String generateMessage(String prompt) {
        // Здесь может быть запрос в свой Chat GPT API
        return "Generated message based on: " + prompt;
    }
}
