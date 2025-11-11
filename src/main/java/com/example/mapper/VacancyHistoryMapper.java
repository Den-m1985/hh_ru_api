package com.example.mapper;

import com.example.dto.VacancyHistoryDto;
import com.example.model.User;
import com.example.model.VacancyHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacancyHistoryMapper {

    public VacancyHistoryDto mapToDto(VacancyHistory history) {
        return new VacancyHistoryDto(
                history.getId(),
                history.getProvider(),
                history.getVacancyUrl(),
                history.getUser().getId()
        );
    }

    public List<VacancyHistoryDto> mapToDto(List<VacancyHistory> list) {
        return list.stream().map(this::mapToDto).toList();
    }

    public VacancyHistory mapToEntity(VacancyHistoryDto dto, User user) {
        VacancyHistory vacancyHistory = new VacancyHistory();
        vacancyHistory.setVacancyUrl(dto.vacancyUrl());
        vacancyHistory.setProvider(dto.provider());
        vacancyHistory.setUser(user);
        return vacancyHistory;
    }
}
