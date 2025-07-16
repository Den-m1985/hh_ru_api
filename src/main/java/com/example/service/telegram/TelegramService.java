package com.example.service.telegram;

import com.example.model.TelegramChat;
import com.example.model.User;
import com.example.repository.TelegramChatRepository;
import com.example.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramChatRepository telegramChatRepository;
    private final TelegramLinkService telegramLinkService;
    private final UserService userService;

    public Optional<TelegramChat> getTelegramChatByUserId(Integer userId) {
        return telegramChatRepository.findByUserId(userId);
    }

    public void bindTelegramChat(Integer userId, Long chatId, Long telegramUserId) {
        User user = userService.getUserById(userId);
        user.setTelegramUserId(telegramUserId);
        TelegramChat telegramChat = user.getTelegramChat();
        if (telegramChat == null) {
            telegramChat = new TelegramChat();
            telegramChat.setUser(user);
        }
        telegramChat.setTelegramChatId(chatId);
        telegramChatRepository.save(telegramChat);
    }

    public String linkAccount(Long chatId, String code, Long telegramUserId) {
        Optional<User> user = userService.getUserByTelegramUserId(telegramUserId);
        if (user.isPresent()){
            TelegramChat telegramChat = user.get().getTelegramChat();
            if (telegramChat.getTelegramChatId().equals(chatId)){
                return "⚠️ Пользователь уже привязан";
            }
        }
        Optional<Integer> userId = telegramLinkService.getUserIdByCode(code);
        if (userId.isPresent()) {
            bindTelegramChat(userId.get(), chatId, telegramUserId);
            return "✅ Успешно! Ваш аккаунт привязан.";
        }
        return "⛔ Неверный или просроченный код.";
    }
}
