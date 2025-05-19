package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import com.example.util.HeadHunterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoverLetterServiceTest {
    private ApplicationProperties applicationProperties;
    private HeadHunterProperties headHunterProperties;
    private GenerateChatClient generateChatClient;
    private CoverLetterService coverLetterService;

    @BeforeEach
    void setUp() {
        applicationProperties = mock(ApplicationProperties.class);
        headHunterProperties = mock(HeadHunterProperties.class);
        generateChatClient = mock(GenerateChatClient.class);
        coverLetterService = new CoverLetterService(applicationProperties, headHunterProperties, generateChatClient);
    }

    @Test
    void testPrepareMessage_WhenUseAi_AndForceMessage() {
        VacancyItem vacancy = mock(VacancyItem.class);
        when(vacancy.name()).thenReturn("Java Developer");
        when(vacancy.response_letter_required()).thenReturn(false);
        when(applicationProperties.getPrePrompt()).thenReturn("Prompt");
        when(generateChatClient.generateMessage("Prompt\n\nJava Developer"))
                .thenReturn("Generated AI message");
        when(headHunterProperties.getUseAi()).thenReturn(true);
        when(headHunterProperties.getForceCoverLetter()).thenReturn(true);

        String result = coverLetterService.prepareMessage(vacancy);

        assertEquals("Generated AI message", result);
        verify(generateChatClient).generateMessage("Prompt\n\nJava Developer");
    }

    @Test
    void testPrepareMessage_WhenNotUsingAi_ButRequiredByVacancy() {
        VacancyItem vacancy = mock(VacancyItem.class);
        when(vacancy.response_letter_required()).thenReturn(true);
        when(headHunterProperties.getCoverLetter()).thenReturn("Predefined cover letter");
        when(headHunterProperties.getUseAi()).thenReturn(false);
        when(headHunterProperties.getForceCoverLetter()).thenReturn(false);

        String result = coverLetterService.prepareMessage(vacancy);

        assertEquals("Predefined cover letter", result);
        verify(headHunterProperties).getCoverLetter();
    }

    @Test
    void testPrepareMessage_WhenNotRequiredAndNotForced() {
        VacancyItem vacancy = mock(VacancyItem.class);
        when(vacancy.response_letter_required()).thenReturn(false);
        when(headHunterProperties.getUseAi()).thenReturn(false);
        when(headHunterProperties.getForceCoverLetter()).thenReturn(false);

        String result = coverLetterService.prepareMessage(vacancy);

        assertEquals("", result);
    }
}
