package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import com.example.util.HeadHunterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoverLetterService {
    private final ApplicationProperties properties;
    private final HeadHunterProperties headHunterProperties;
    private final GenerateChatClient blackboxChatClient;

    public String prepareMessage(VacancyItem vacancy) {
        if (Boolean.TRUE.equals(headHunterProperties.getForceCoverLetter()) || Boolean.TRUE.equals(vacancy.response_letter_required())) {
            return Boolean.TRUE.equals(headHunterProperties.getUseAi())
                    ? blackboxChatClient.generateMessage(properties.getPrePrompt() + "\n\n" + vacancy.name())
                    : headHunterProperties.getCoverLetter();
        }
        return "";
    }

}
