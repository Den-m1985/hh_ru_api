package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyFilter {

    public List<VacancyItem> filterVacancies(Set<VacancyItem> allVacancies, List<String> keywordsToExclude) {
        return allVacancies.stream()
                .filter(this::isNotNegotiated)
                .filter(this::isNotArchivedOrWithTest)
                .filter(vacancy -> doesNotContainExcludedKeywords(vacancy, keywordsToExclude))
                .toList();
    }

    // Фильтрация вакансий, по которым уже есть отклики (переговоры)
    private boolean isNotNegotiated(VacancyItem vacancy) {
        if (vacancy.relations().isEmpty()) {
            return true;
        }
        log.debug("Skipping vacancy {} with negotiation: {}", vacancy.id(), vacancy.relations());
        return false;
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
    private boolean doesNotContainExcludedKeywords(VacancyItem vacancy, List<String> keywordsToExclude) {
        String name = vacancy.name().toLowerCase();
        boolean hasKeyword = keywordsToExclude.stream()
                .anyMatch(keyword -> name.contains(keyword.toLowerCase()));
        if (hasKeyword) {
            log.debug("Skipping vacancy {} by keyword match: {}", vacancy.id(), vacancy.name());
        }
        return !hasKeyword;
    }

}
