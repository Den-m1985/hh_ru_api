package com.example.service.superjob;

import com.example.dto.superjob.Vacancy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperjobVacancyFilter {

    public List<Vacancy> filteredVacancies(Set<Vacancy> allVacancies, List<String> keywordsToExclude) {
        List<Vacancy> filtered = new ArrayList<>();

        allVacancies.forEach(vacancy -> {
            if (isNegotiated(vacancy)) {
                log.debug("Skipping vacancy {} with negotiation", vacancy.id());
                return;
            }
            if (isArchived(vacancy)) {
                log.debug("Skipping archived vacancy: {}, {}", vacancy.id(), vacancy.profession());
                return;
            }
            if (isContainExcludedKeywords(vacancy, keywordsToExclude)) {
                log.debug("Skipping vacancy {} by keyword match: {}", vacancy.id(), vacancy.profession());
                return;
            }
            filtered.add(vacancy);
        });
        return filtered;
    }

    // Фильтрация вакансий, по которым уже есть отклики (переговоры)
    private boolean isNegotiated(Vacancy vacancy) {
        return Boolean.TRUE.equals(vacancy.already_sent_on_vacancy());
    }

    private boolean isArchived(Vacancy vacancy) {
        return Boolean.TRUE.equals(vacancy.is_archive());
    }

    private boolean isContainExcludedKeywords(Vacancy vacancy, List<String> keywordsToExclude) {
        String name = vacancy.profession().toLowerCase();
        return keywordsToExclude.stream()
                .anyMatch(keyword -> name.contains(keyword.toLowerCase()));
    }
}
