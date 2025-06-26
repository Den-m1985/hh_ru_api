package com.example.service.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class TelegramKeyboardFactory {

    public ReplyKeyboardMarkup mainMenuKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add("/start");
        row.add("/link");

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true); // Не скрывать клавиатуру после использования false
        return markup;
    }

}
