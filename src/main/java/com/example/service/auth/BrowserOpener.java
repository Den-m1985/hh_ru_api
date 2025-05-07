package com.example.service.auth;

import java.awt.*;
import java.net.URI;

public class BrowserOpener {

    public static void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Пожалуйста, откройте в браузере эту ссылку для авторизации:");
                System.out.println(url);

            }
        } catch (Exception e) {
            System.out.println("Не удалось открыть браузер автоматически.");
            System.out.println("Откройте вручную: " + url);
        }
    }
}
