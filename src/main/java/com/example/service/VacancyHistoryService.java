package com.example.service;

import com.example.dto.VacancyHistoryDto;
import com.example.mapper.VacancyHistoryMapper;
import com.example.model.User;
import com.example.model.VacancyHistory;
import com.example.repository.VacancyHistoryRepository;
import com.example.service.common.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VacancyHistoryService {
    VacancyHistoryRepository historyRepository;
    UserService userService;
    VacancyHistoryMapper mapper;

    public VacancyHistory getVacancyHistoryById(Integer id) {
        return historyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vacancy history not found"));
    }

    public List<VacancyHistoryDto> getVacanciesByProvider(String provider) {
        List<VacancyHistory> array = historyRepository.findByProvider(provider);
        return mapper.mapToDto(array);
    }

    public List<VacancyHistoryDto> getVacanciesHistoryByUser(Integer userId) {
        List<VacancyHistory> array = historyRepository.getVacancyHistoryByUser(userId);
        return mapper.mapToDto(array);
    }

    @Transactional
    public VacancyHistory addHistory(VacancyHistoryDto dto, User user) {
        VacancyHistory vacancyHistory = mapper.mapToEntity(dto, user);
        return historyRepository.save(vacancyHistory);
    }

    @Transactional
    public VacancyHistory addHistory(VacancyHistoryDto dto) {
        User user = userService.getUserById(dto.userId());
        VacancyHistory vacancyHistory = mapper.mapToEntity(dto, user);
        return historyRepository.save(vacancyHistory);
    }

    @Transactional
    public void deleteHistory(Integer id) {
        historyRepository.deleteById(id);
    }
}
