package com.example.service.telegram;

import com.example.util.TelegramProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class MyTelegramBot extends TelegramLongPollingBot {
    private final TelegramProperties telegramProperties;
    private final TelegramService telegramService;
    private final TelegramKeyboardFactory keyboardFactory;

    @Override
    public String getBotUsername() {
        return telegramProperties.getUsername();
    }

    public MyTelegramBot(TelegramProperties telegramProperties,
                         TelegramService telegramService,
                         TelegramKeyboardFactory keyboardFactory) {
        super(telegramProperties.getToken());
        this.telegramProperties = telegramProperties;
        this.telegramService = telegramService;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            Long telegramUserId = message.getFrom().getId();

            try {
                if ("/start".equals(text)) {
                    sendStartMessage(chatId);
                } else if (text.startsWith("/link ")) {
                    handleLinkCommand(chatId, text.substring(6).trim(), telegramUserId);
                } else {
                    sendMessage(chatId, "Используйте /start для инструкций");
                }
            } catch (Exception e) {
                log.error("Error processing message: {}, {}", text, e.getMessage());
                sendMessage(chatId, "⚠️ Ошибка обработки запроса");
            }
        }
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
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("chatId: {}, error message: {}", chatId, e.getMessage());
        }
    }

    private void handleLinkCommand(Long chatId, String code, Long telegramUserId) {
        String resultMessage = telegramService.linkAccount(chatId, code, telegramUserId);
        sendMessage(chatId, resultMessage);
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
