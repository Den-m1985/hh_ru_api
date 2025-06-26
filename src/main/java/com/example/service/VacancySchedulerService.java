package com.example.service;

import com.example.dto.VacancyRequest;
import com.example.model.AuthUser;
import com.example.model.AutoResponseSchedule;
import com.example.model.User;
import com.example.repository.AutoResponseScheduleRepository;
import com.example.repository.UserRepository;
import com.example.service.common.UserService;
import com.example.service.notify.TelegramUserNotifier;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancySchedulerService {
    private final AutoResponseScheduleRepository scheduleRepository;
    private final VacancyResponseProcessor processor;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TelegramUserNotifier telegramUserNotifier;

    // Используем Caffeine Cache для автоматического управления памятью
    private Cache<Integer, VacancyRequest> memoryCache;

    @PostConstruct
    public void init() {
        this.memoryCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(25, TimeUnit.HOURS) // Например, удаляем через 25 часов бездействия
                .build();
        loadActiveSchedulesIntoCache();
        log.info("Инициализировано {} автоответчиков в память при запуске.", memoryCache.estimatedSize());
    }

    private void loadActiveSchedulesIntoCache() {
        memoryCache.invalidateAll();
        List<AutoResponseSchedule> enabledSchedules = scheduleRepository.findAllByEnabledTrue();
        for (AutoResponseSchedule schedule : enabledSchedules) {
            memoryCache.put(schedule.getUser().getId(), schedule.getParams());
        }
    }

    public void updateSchedule(VacancyRequest request, AuthUser authUser) {
        Integer userId = authUser.getUser().getId();
        User user = userService.getUserById(userId);

        AutoResponseSchedule schedule = scheduleRepository.findByUserId(userId)
                .orElseGet(() -> {
                    AutoResponseSchedule newSchedule = new AutoResponseSchedule();
                    newSchedule.setUser(user);
                    return newSchedule;
                });
        schedule.setUser(user);
        schedule.setEnabled(request.enabledSchedule());
        schedule.setParams(request);
        scheduleRepository.save(schedule);
        user.setAutoResponseSchedule(schedule);

        if (request.enabledSchedule()) {
            memoryCache.put(userId, request);
        } else {
            memoryCache.invalidate(userId);
        }
        log.info("Планировщик {} для userId={} обновлён: enabled={}",
                schedule.getId(), userId, request.enabledSchedule());
    }

    /**
     * cron выражения по умолчанию шестизначные: секунда, минута, час, день месяца, месяц, день недели.
     */
    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Moscow") // каждый день в 10 утра по мск
    public void executeScheduledResponses() {
        log.info("Запуск выполнения запланированных автооткликов. Текущий размер кэша: {}", memoryCache.estimatedSize());

        loadActiveSchedulesIntoCache();
        log.info("Кэш автоответчиков синхронизирован. Активных расписаний: {}", memoryCache.estimatedSize());
        for (Map.Entry<Integer, VacancyRequest> entry : memoryCache.asMap().entrySet()) {
            Integer userId = entry.getKey();
            VacancyRequest request = entry.getValue();

            userRepository.findById(userId).ifPresent(user -> {
                try {
                    processor.respondToRelevantVacancies(request, userId);
                    log.info("✅ Автоотклик выполнен для userId={}", userId);
                    telegramUserNotifier.notifyUser(userId, "✅ Автоотклик выполнен");
                } catch (Exception e) {
                    log.error("❌ Ошибка автоотклика для userId={}: {}", userId, e.getMessage(), e);
                }
            });
        }
        log.info("Выполнение запланированных автооткликов завершено.");
    }
}
