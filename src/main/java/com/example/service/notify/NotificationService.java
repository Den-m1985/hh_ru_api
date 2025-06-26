package com.example.service.notify;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final List<UserNotifier> notifiers;

    public void notifyUser(Integer userId, String message) {
        for (UserNotifier notifier : notifiers) {
            notifier.notifyUser(userId, message);
        }
    }
}
