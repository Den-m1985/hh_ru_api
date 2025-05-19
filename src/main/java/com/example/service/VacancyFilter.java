package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyFilter {
    private final NegotiationsAll negotiationsAll;

    public List<VacancyItem> filterVacancies(Set<VacancyItem> allVacancies) {
        return allVacancies.stream()
                .filter(this::isNotNegotiated)
                .filter(this::isNotArchivedOrWithTest)
                .filter(this::doesNotContainExcludedKeywords)
                .toList();
    }

    // Фильтрация вакансий, по которым уже есть отклики (переговоры)
    private boolean isNotNegotiated(VacancyItem vacancy) {
        Set<String> negotiationIds = new HashSet<>(negotiationsAll.getNegotiationList());
        boolean isNegotiated = negotiationIds.contains(vacancy.id());
        if (isNegotiated) {
            log.debug("Skipping vacancy {} already negotiated: {}", vacancy.id(), vacancy.name());
        }
        return !isNegotiated;
    }

    // Проверка на архивность и наличие теста
    private boolean isNotArchivedOrWithTest(VacancyItem vacancy) {
        if (vacancy.has_test() || vacancy.archived()) {
            log.debug("Skipping vacancy {} with test or archived: {}", vacancy.id(), vacancy.name());
            return false;
        }
        return true;
    }

    // Проверка на наличие исключающих ключевых слов
    private boolean doesNotContainExcludedKeywords(VacancyItem vacancy) {
        List<String> keywordsToExclude = List.of("Senior", "lead", "TeamLead", "Android", "QA", "Python",
                "Typescript", "Go", "DevOps", "Oracle", "Node.js", "ReactJS", "Менеджер", "Лектор", "Ведущий", "Старший",
                "Тестировщик", "Fullstack", "фулстек", "Автотестировщик", "Главный", "Руководитель");
        String name = vacancy.name().toLowerCase();
        boolean hasKeyword = keywordsToExclude.stream()
                .anyMatch(keyword -> name.contains(keyword.toLowerCase()));
        if (hasKeyword) {
            log.debug("Skipping vacancy {} by keyword match: {}", vacancy.id(), vacancy.name());
        }
        return !hasKeyword;
    }

}
