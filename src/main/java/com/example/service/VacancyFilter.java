package com.example.service;

import com.example.dto.FilterResult;
import com.example.dto.vacancy_dto.VacancyItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyFilter {

    public FilterResult filterVacancies(Set<VacancyItem> allVacancies, List<String> keywordsToExclude) {
        List<VacancyItem> filtered = new ArrayList<>();
        List<VacancyItem> withTests = new ArrayList<>();

        allVacancies.forEach(vacancy -> {
            if (isNegotiated(vacancy)) {
                log.debug("Skipping vacancy {} with negotiation: {}", vacancy.id(), vacancy.relations());
                return;
            }
            if (isArchived(vacancy)) {
                log.debug("Skipping archived vacancy: {}, {}", vacancy.id(), vacancy.name());
                return;
            }
            if (isContainExcludedKeywords(vacancy, keywordsToExclude)) {
                log.debug("Skipping vacancy {} by keyword match: {}", vacancy.id(), vacancy.name());
                return;
            }
            if (hasTest(vacancy)) {
                withTests.add(vacancy);
                log.debug("Vacancy with test added to separate list: {}", vacancy.id());
                return;
            }
            filtered.add(vacancy);
        });
        return new FilterResult(filtered, withTests);
    }

    // Фильтрация вакансий, по которым уже есть отклики (переговоры)
    private boolean isNegotiated(VacancyItem vacancy) {
        return !vacancy.relations().isEmpty();
    }

    private boolean isArchived(VacancyItem vacancy) {
        return Boolean.TRUE.equals(vacancy.archived());
    }

    private boolean isContainExcludedKeywords(VacancyItem vacancy, List<String> keywordsToExclude) {
        String name = vacancy.name().toLowerCase();
        return keywordsToExclude.stream()
                .anyMatch(keyword -> name.contains(keyword.toLowerCase()));
    }

    private boolean hasTest(VacancyItem vacancy) {
        return Boolean.TRUE.equals(vacancy.has_test());
    }
}
