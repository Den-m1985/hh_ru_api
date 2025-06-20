package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoverLetterService {
    private final ApplicationProperties properties;
    private final GenerateChatClient generateChatClient;
    private boolean useAi = false;
    private boolean forceCoverLetter = true;

    public String prepareMessage(VacancyItem vacancy, String coverLetter) {
        if (forceCoverLetter || Boolean.TRUE.equals(vacancy.response_letter_required())) {
            return useAi
                    ? generateChatClient.generateMessage(properties.getPrePrompt() + "\n\n" + vacancy.name())
                    : coverLetter;
        }
        return "";
    }

}
