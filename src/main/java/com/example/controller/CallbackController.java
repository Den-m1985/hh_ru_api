package com.example.controller;

import com.example.controller.interfaces.CallBackApi;
import com.example.enums.ApiProvider;
import com.example.service.CallBackService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/callback")
public class CallbackController implements CallBackApi {
    private final CallBackService callBackService;

    // TODO применить стратегию и фабрику через интерфейс

    @GetMapping
    public ResponseEntity<String> callback(@RequestParam String code, @RequestParam String state) {
        callBackService.authenticate(ApiProvider.HEADHUNTER, code, state);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        String htmlContent = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <title>Авторизация прошла успешно</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                            background-color: #f0f2f5;
                            color: #333;
                        }
                        .container {
                            text-align: center;
                            padding: 20px;
                            border-radius: 8px;
                            background-color: white;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <p>Авторизация прошла успешно. Это окно закроется автоматически.</p>
                    </div>
                    <script>
                        if (window.opener) {
                            // Отправляем сообщение родительскому окну, если оно есть
                            window.opener.postMessage('hh_auth_success', '*');
                            window.close(); // Закрываем текущее окно
                        } else {
                            // Если страница открыта напрямую, показываем сообщение об ошибке
                            document.body.innerHTML = '<div class="container"><p>Ошибка: Эта страница должна быть открыта из основного приложения.</p></div>';
                        }
                    </script>
                </body>
                </html>
                """;
        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
    }

    @GetMapping("/superjob")
    public ResponseEntity<String> callbackSuperJob(
            @RequestParam @NotBlank String code,
            @RequestParam @NotBlank String state
    ) {
        callBackService.authenticate(ApiProvider.SUPERJOB, code, state);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        String htmlContent = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <title>Авторизация прошла успешно</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                            background-color: #f0f2f5;
                            color: #333;
                        }
                        .container {
                            text-align: center;
                            padding: 20px;
                            border-radius: 8px;
                            background-color: white;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <p>Авторизация прошла успешно. Это окно закроется автоматически.</p>
                    </div>
                    <script>
                        if (window.opener) {
                            // Отправляем сообщение родительскому окну, если оно есть
                            window.opener.postMessage('sj_auth_success', '*');
                            window.close(); // Закрываем текущее окно
                        } else {
                            // Если страница открыта напрямую, показываем сообщение об ошибке
                            document.body.innerHTML = '<div class="container"><p>Ошибка: Эта страница должна быть открыта из основного приложения.</p></div>';
                        }
                    </script>
                </body>
                </html>
                """;
        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
    }

}
