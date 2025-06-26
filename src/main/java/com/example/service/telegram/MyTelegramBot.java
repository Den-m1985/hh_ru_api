package com.example.service.telegram;

import com.example.util.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {
    private final TelegramProperties telegramProperties;
    private final TelegramLinkService telegramLinkService;
    private final TelegramService telegramService;
    private final TelegramKeyboardFactory keyboardFactory;


    @Override
    public String getBotUsername() {
        return telegramProperties.getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getBot().getToken();
    }

    public void sendStartMessage(Long chatId) {
        String welcomeText = """
                👋 Добро пожаловать!
                
                Этот бот позволяет получать уведомления от сервиса:
                
                Job Responder
                
                Чтобы привязать аккаунт, выполните следующие шаги:
                
                1. Перейдите в Postman или frontend
                2. Выполните GET-запрос к /v1/telegram/link
                3. Вы получите код вида: `2e310a`
                4. Введите в боте:
                `/link 2e310a`
                """;
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(welcomeText);
        message.setParseMode("Markdown");

        message.setReplyMarkup(keyboardFactory.mainMenuKeyboard());
        // Добавляем кнопку "Привязать аккаунт"
//        message.setReplyMarkup(keyboardFactory.linkButtonKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("chatId: {}, error message: {}", chatId, e.getMessage());
        }
    }

    // Обработка входящих сообщений
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            Long telegramUserId = message.getFrom().getId();

            switch (text) {
                case "/start" -> sendStartMessage(chatId);
                case "/link" -> sendMessage(chatId, "Введите команду вида:\n/link <код>");
                default -> {
                    if (text.startsWith("/link ")) {
                        String code = text.substring(6).trim();
                        telegramLinkService.getUserIdByCode(code).ifPresentOrElse(userId -> {
                            telegramService.bindTelegramChat(userId, chatId, telegramUserId);
                            sendMessage(chatId, "✅ Успешно! Ваш аккаунт привязан.");
                        }, () -> sendMessage(chatId, "⛔ Неверный или просроченный код."));
                    } else {
                        sendMessage(chatId, "Неизвестная команда. Нажмите /start для меню.");
                    }
                }
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
            log.error(e.getMessage());
        }
    }

}
