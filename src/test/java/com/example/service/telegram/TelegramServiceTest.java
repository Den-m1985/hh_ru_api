package com.example.service.telegram;

import com.example.dto.AuthResponse;
import com.example.dto.UserDTO;
import com.example.model.TelegramChat;
import com.example.model.User;
import com.example.repository.TelegramChatRepository;
import com.example.repository.UserRepository;
import com.example.service.common.RegisterService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class TelegramServiceTest {
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private RegisterService registerService;
    @MockitoBean
    TelegramLinkService telegramLinkService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramChatRepository telegramChatRepository;
    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    private User testUser;
    private User testUser2;

    private final Long testChatId = 12345L;
    private final Long testTelegramUserId = 67890L;
    private final String validCode = "VALID_CODE";

    @BeforeEach
    void setUp() {
        telegramChatRepository.deleteAll();
        userRepository.deleteAll();

        String email = "john.doe@example.com";
        String password = "password";
        UserDTO request = new UserDTO(email, password);
        AuthResponse response = registerService.registerUser(request, servletResponse);
        assertNotNull(response.userId());
        testUser = userRepository.findByEmail(email).orElse(null);

        String email2 = "john.doe@example2.com";
        UserDTO request2 = new UserDTO(email2, password);
        AuthResponse response2 = registerService.registerUser(request2, servletResponse);
        assertNotNull(response2.userId());
        testUser2 = userRepository.findByEmail(email2).orElse(null);
    }

    @Test
    void shouldLinkAccountWithValidCodeAndNoExistingChat() {
        when(telegramLinkService.getUserIdByCode(anyString())).thenReturn(Optional.of(testUser.getId()));

        String result = telegramService.linkAccount(testChatId, validCode, testTelegramUserId);

        assertEquals("✅ Успешно! Ваш аккаунт привязан.", result);
    }

    @Test
    void shouldReturnAlreadyLinkedIfTelegramUserIsPresent() {
        TelegramChat telegramChat = new TelegramChat();
        telegramChat.setTelegramChatId(testChatId);
        telegramChat.setUser(testUser);
        telegramChatRepository.save(telegramChat);
        testUser.setTelegramChat(telegramChat);
        testUser.setTelegramUserId(testTelegramUserId);
        userRepository.save(testUser);
        when(telegramLinkService.getUserIdByCode(anyString())).thenReturn(Optional.of(testUser2.getId()));

        String result = telegramService.linkAccount(testChatId, validCode, testTelegramUserId);

        assertEquals("⚠️ Пользователь уже привязан", result);
    }
}
