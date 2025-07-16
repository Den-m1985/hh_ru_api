package com.example.controller.interfaces;

import com.example.dto.JwtAuthResponse;
import com.example.dto.TelegramAuthRequest;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Telegram API", description = "Регистрация и аутентификация через Telegram")
public interface TelegramApi {

    @Operation(
            summary = "Привязка телеграм бота к пользователю",
            description = "Возвращает код который надо ввести в телеграм боте"
    )
    ResponseEntity<String> generateLinkCode(@AuthenticationPrincipal AuthUser authUser);

    @Operation(
            summary = "Регистрация и аутентификация",
            description = """
        ## Описание работы:
        1. Принимает данные авторизации от Telegram Widget
        2. Проверяет валидность данных с помощью хеша
        3. Если пользователь с таким telegramUserId существует:
           - Обновляет username, если он изменился в Telegram
           - Возвращает JWT токены
        4. Если пользователь не существует:
           - Создает нового пользователя
           - Генерирует username (использует telegram username или создает вида 'telegram_12345')
           - Устанавливает роль USER
           - Возвращает JWT токены
        """
    )
    ResponseEntity<JwtAuthResponse> authViaTelegram(
            @RequestBody TelegramAuthRequest telegramAuthRequest,
            HttpServletResponse response
    );
}
