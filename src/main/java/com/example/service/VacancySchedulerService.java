package com.example.service;

import com.example.dto.AutoResponseScheduleDto;
import com.example.dto.VacancyRequest;
import com.example.model.AutoResponseSchedule;
import com.example.model.User;
import com.example.repository.AutoResponseScheduleRepository;
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
public class VacancySchedulerService {
    private final AutoResponseScheduleRepository scheduleRepository;
    private final VacancyResponseProcessor processor;
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
        log.info("Инициализировано {} автоответчиков hh в память при запуске.", memoryCache.estimatedSize());
    }

    private void loadActiveSchedulesIntoCache() {
        memoryCache.invalidateAll();
        List<AutoResponseSchedule> enabledSchedules = scheduleRepository.findAllByEnabledTrue();
        for (AutoResponseSchedule schedule : enabledSchedules) {
            Optional<VacancyRequest> request = schedule.getParams(VacancyRequest.class);
            request.ifPresent(
                    vacancyRequest -> memoryCache.put(schedule.getId(), vacancyRequest)
            );
        }
    }


    @Transactional
    public AutoResponseScheduleDto createOrUpdateSchedule(VacancyRequest request, User user) {
        if (request.nameRequest() == null || request.nameRequest().isEmpty()) {
            throw new IllegalArgumentException("Имя расписания не может быть пустым");
        } else if (request.nameRequest().length() > 100) {
            throw new IllegalArgumentException("Имя расписания слишком длинное (макс. 100 символов)");
        }

        log.info("\"VacancyRequest request\" from client to save: {}", request);

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
        log.info("Create a new hh schedule: {} for userId: {}", schedule.getId(), user.getId());

        if (schedule.isEnabled()) {
            memoryCache.put(schedule.getId(), schedule.getParams(VacancyRequest.class).get());
        } else {
            memoryCache.invalidate(schedule.getId());
        }

        log.info("Расписание {} id: {} для userId: {} обновлено/создано: enabled: {}",
                schedule.getName(), schedule.getId(), user.getId(), schedule.isEnabled());
        return AutoResponseScheduleDto.fromEntity(schedule);
    }

    public void deleteScheduleById(Integer scheduleId, User user) {
        AutoResponseSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Расписание=" + scheduleId + " для userId=" + user.getId() + " не найдено"));
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User: " + user.getId() + " пытался удалить чужое расписание:" + scheduleId);
        }
        scheduleRepository.delete(schedule);
        memoryCache.invalidate(schedule.getUser().getId());
        log.info("Удалено расписание hh id: {} для userId: {}", scheduleId, schedule.getUser().getId());
    }

    public List<AutoResponseScheduleDto> getAllSchedulesByUser(User user) {
        Integer userId = user.getId();
        return scheduleRepository.findAllByUserId(userId).stream()
                .map(AutoResponseScheduleDto::fromEntity)
                .toList();
    }

    public AutoResponseScheduleDto getScheduleById(Integer scheduleId, User user) {
        AutoResponseSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Расписание hh: " + scheduleId + " для userId=" + user.getId() + " не найдено"));
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User: " + user.getId() + " пытался просматривать чужое расписание");
        }
        return AutoResponseScheduleDto.fromEntity(schedule);
    }

    public AutoResponseScheduleDto getScheduleByName(String scheduleName, User user) {
        AutoResponseSchedule schedule = scheduleRepository.findByNameAndUserId(scheduleName, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Расписание hh: " + scheduleName + " не найдено"));
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User: " + user.getId() + " пытался просматривать чужое расписание");
        }
        return AutoResponseScheduleDto.fromEntity(schedule);
    }

    /**
     * cron выражения по умолчанию шестизначные: секунда, минута, час, день месяца, месяц, день недели.
     */
    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Moscow") // каждый день в 10 утра по мск
    public void executeScheduledResponses() {
        log.info("Запуск выполнения запланированных автооткликов hh. Текущий размер кэша: {}", memoryCache.estimatedSize());

        loadActiveSchedulesIntoCache();
        log.info("Кэш автоответчиков hh синхронизирован. Активных расписаний: {}", memoryCache.estimatedSize());
        for (Map.Entry<Integer, VacancyRequest> entry : memoryCache.asMap().entrySet()) {
            Integer scheduleId = entry.getKey();
            VacancyRequest request = entry.getValue();

            scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
                Integer userId = schedule.getUser().getId();
                try {
                    processor.respondToRelevantVacancies(request, userId);
                    log.info("✅ Автоотклик hh выполнен для userId={}", userId);
                    telegramUserNotifier.notifyUser(userId, "✅ Автоотклик hh выполнен");
                } catch (Exception e) {
                    log.error("❌ Ошибка автоотклика hh для userId={}: {}", userId, e.getMessage(), e);
                }
            });
        }
        log.info("Выполнение запланированных автооткликов hh завершено.");
    }
}
