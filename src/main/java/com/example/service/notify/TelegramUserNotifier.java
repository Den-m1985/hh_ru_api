package com.example.service.notify;

import com.example.service.telegram.MyTelegramBot;
import com.example.service.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserNotifier implements UserNotifier {
    private final TelegramService telegramService;
    private final MyTelegramBot telegramBot;


    @Override
    public void notifyUser(Integer userId, String message) {
        telegramService.getTelegramChatByUserId(userId).ifPresentOrElse(chat -> {
            Long chatId = chat.getTelegramChatId();
            telegramBot.sendMessage(chatId, message);
        }, () -> log.warn("Не найден Telegram-чат для пользователя {}", userId));
    }
}
