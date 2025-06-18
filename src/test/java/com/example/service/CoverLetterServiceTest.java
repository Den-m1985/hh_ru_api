package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoverLetterServiceTest {
    private ApplicationProperties applicationProperties;
    private GenerateChatClient generateChatClient;
    private CoverLetterService coverLetterService;

    @BeforeEach
    void setUp() {
        applicationProperties = mock(ApplicationProperties.class);
        generateChatClient = mock(GenerateChatClient.class);
        coverLetterService = new CoverLetterService(applicationProperties, generateChatClient);
    }

    @Test
    void testPrepareMessage_WhenUseAi_AndForceMessage() {
        VacancyItem vacancy = mock(VacancyItem.class);
        when(vacancy.name()).thenReturn("Java Developer");
        when(vacancy.response_letter_required()).thenReturn(false);
        when(applicationProperties.getPrePrompt()).thenReturn("Prompt");
        when(generateChatClient.generateMessage("Prompt\n\nJava Developer"))
                .thenReturn("Generated AI message");
        try {
            Field field = CoverLetterService.class.getDeclaredField("useAi");
            field.setAccessible(true);
            field.set(coverLetterService, true);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить updatedAt через reflection", e);
        }

        String result = coverLetterService.prepareMessage(vacancy, "");

        assertEquals("Generated AI message", result);
        verify(generateChatClient).generateMessage("Prompt\n\nJava Developer");
    }

    @Test
    void testPrepareMessage_WhenNotUsingAi_ButRequiredByVacancy() {
        VacancyItem vacancy = mock(VacancyItem.class);
        when(vacancy.response_letter_required()).thenReturn(true);

        String result = coverLetterService.prepareMessage(vacancy, "Predefined cover letter");

        assertEquals("Predefined cover letter", result);
    }

    @Test
    void testPrepareMessage_WhenNotRequiredAndNotForced() {
        VacancyItem vacancy = mock(VacancyItem.class);
        when(vacancy.response_letter_required()).thenReturn(false);

        String result = coverLetterService.prepareMessage(vacancy, "");

        assertEquals("", result);
    }
}
