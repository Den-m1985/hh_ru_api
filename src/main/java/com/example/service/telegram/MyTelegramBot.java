package com.example.service.telegram;

import com.example.util.TelegramProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {
    private final TelegramProperties telegramProperties;
    private final TelegramLinkService telegramLinkService;
    private final TelegramService telegramService;

//    @Value("${telegram.bot.token}")
//    private String botToken;
//
//    @Value("${telegram.bot.username}")
//    private String botUsername;


    @Override
    public String getBotUsername() {
        return telegramProperties.getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getBot().getToken();
    }

    // Обработка входящих сообщений
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            Long telegramUserId = message.getFrom().getId(); // Получаем telegramUserId

            if (text.startsWith("/link ")) {
                String code = text.substring(6).trim();
                telegramLinkService.getUserIdByCode(code).ifPresentOrElse(userId -> {
                    telegramService.bindTelegramChat(userId, chatId, telegramUserId);
                    sendMessage(chatId, "✅ Успешно! Ваш аккаунт привязан.");
                }, () -> sendMessage(chatId, "⛔ Неверный или просроченный код."));
            } else {
                sendMessage(chatId, "Введите команду вида:\n/link <код>");
            }
        }
    }

    public void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
