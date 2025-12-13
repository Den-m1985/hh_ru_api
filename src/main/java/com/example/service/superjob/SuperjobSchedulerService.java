package com.example.service.superjob;

import com.example.dto.AutoResponseScheduleDto;
import com.example.dto.superjob.SuperjobVacancyRequest;
import com.example.model.AutoResponseSchedule;
import com.example.model.SuperjobResume;
import com.example.model.User;
import com.example.repository.AutoResponseScheduleRepository;
import com.example.service.common.UserService;
import com.example.service.notify.TelegramUserNotifier;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuperjobSchedulerService {
    private final AutoResponseScheduleRepository scheduleRepository;
    private final TelegramUserNotifier telegramUserNotifier;
    private final ClientSuperjob clientSuperjob;
    private final UserService userService;

    // Используем Caffeine Cache для автоматического управления памятью
    private Cache<Integer, SuperjobVacancyRequest> memoryCache;

    @PostConstruct
    public void init() {
        this.memoryCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(25, TimeUnit.HOURS) // Например, удаляем через 25 часов бездействия
                .build();
        loadActiveSchedulesIntoCache();
        log.info("Инициализировано {} автоответчиков sj в память при запуске.", memoryCache.estimatedSize());
    }

    private void loadActiveSchedulesIntoCache() {
        memoryCache.invalidateAll();
        List<AutoResponseSchedule> enabledSchedules = scheduleRepository.findAllByEnabledTrue();
        for (AutoResponseSchedule schedule : enabledSchedules) {
            Optional<SuperjobVacancyRequest> request = schedule.getParams(SuperjobVacancyRequest.class);
            request.ifPresent(
                    vacancyRequest -> memoryCache.put(schedule.getId(), vacancyRequest)
            );
        }
    }


    @Transactional
    public AutoResponseScheduleDto createOrUpdateSchedule(SuperjobVacancyRequest request, User user) {
        hasResume(user);
        List<AutoResponseSchedule> existingSchedule = scheduleRepository.findByUserId(user.getId());
        AutoResponseSchedule schedule = null;
        boolean isFind = false;

        for (AutoResponseSchedule autoResponseSchedule : existingSchedule) {
            String name = autoResponseSchedule.getName();
            if (request.nameRequest().equals(name)) {
                autoResponseSchedule.setParams(request);
                autoResponseSchedule.setEnabled(request.enabledSchedule());
                scheduleRepository.save(autoResponseSchedule);
                schedule = autoResponseSchedule;
                isFind = true;
                break;
            }
        }
        if (!isFind) {
            schedule = new AutoResponseSchedule();
            schedule.setUser(user);
            schedule.setName(request.nameRequest());
            schedule.setEnabled(request.enabledSchedule());
            schedule.setParams(request);
            schedule = scheduleRepository.save(schedule);
            existingSchedule.add(schedule);
        }
        log.info("Create a new sj schedule {} for userId: {}", schedule.getId(), user.getId());

        if (schedule.isEnabled()) {
            memoryCache.put(schedule.getId(), schedule.getParams(SuperjobVacancyRequest.class).get());
        } else {
            memoryCache.invalidate(schedule.getId());
        }

        log.info("Расписание: {} id: {} для userId: {} обновлено/создано: enabled: {}",
                schedule.getName(), schedule.getId(), user.getId(), schedule.isEnabled());
        return AutoResponseScheduleDto.fromEntity(schedule);
    }

    public void hasResume(User user) {
        user = userService.getUserById(user.getId());
        List<SuperjobResume> superjobResumes = user.getSuperjobResumes();
        if (superjobResumes.isEmpty()) {
            throw new EntityNotFoundException("User: " + user.getId() + " don't have resume");
        }
    }

    /**
     * cron выражения по умолчанию шестизначные: секунда, минута, час, день месяца, месяц, день недели.
     */
    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Moscow") // каждый день в 10 утра по мск
    public void executeScheduledResponses() {
        log.info("Запуск выполнения запланированных автооткликов sj. Текущий размер кэша: {}", memoryCache.estimatedSize());
        loadActiveSchedulesIntoCache();
        log.info("Кэш автоответчиков sj синхронизирован. Активных расписаний: {}", memoryCache.estimatedSize());

        for (Map.Entry<Integer, SuperjobVacancyRequest> entry : memoryCache.asMap().entrySet()) {
            SuperjobVacancyRequest request = entry.getValue();
            Integer scheduleId = entry.getKey();
            scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
                User user = schedule.getUser();
                try {

                    clientSuperjob.sendCvOnVacancy(request, user);

                    log.info("✅ Автоотклик sj выполнен для userId: {}", user.getId());
                    telegramUserNotifier.notifyUser(user.getId(), "✅ Автоотклик sj выполнен");
                } catch (Exception e) {
                    log.error("❌ Ошибка автоотклика sj для userId: {}: {}", user.getId(), e.getMessage(), e);
                }
            });
        }
        log.info("Выполнение запланированных автооткликов sj завершено.");
    }
}
