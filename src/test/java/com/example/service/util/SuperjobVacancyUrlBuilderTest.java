package com.example.service.util;

import com.example.dto.superjob.Keyword;
import com.example.dto.superjob.SuperjobSearchRequest;
import com.example.util.SuperjobVacancyUrlBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SuperjobVacancyUrlBuilderTest {
    private final String baseUrl = "https://api.superjob.ru";
    private final SuperjobVacancyUrlBuilder builder = new SuperjobVacancyUrlBuilder();

    @Test
    void shouldBuildUrlWithKeywordOnly() {
        SuperjobSearchRequest request = new SuperjobSearchRequest(
                null, null, null, null, null, null, null, null, null,
                null, null,
                "java",
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
        );

        String url = builder.buildUrl(request, baseUrl);

        assertThat(url)
                .isEqualTo("https://api.superjob.ru/2.0/vacancies/?keyword=java");
    }

    @Test
    void shouldBuildUrlWithPaymentRange() {
        SuperjobSearchRequest request = new SuperjobSearchRequest(
                null, null, null, null, null, null, null, null, null,
                null, null,
                "java",
                null, null, null, null, 1000, 5000, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
        );

        String url = builder.buildUrl(request, baseUrl);

        assertThat(url)
                .contains("keyword=java")
                .contains("payment_from=1000")
                .contains("payment_to=5000");
    }

    @Test
    void shouldBuildUrlWithKeywordsList() {
        List<Keyword> array = List.of(new Keyword(1, "and", "php"), new Keyword(3, "particular", "javascript"));
        SuperjobSearchRequest request = new SuperjobSearchRequest(
                null, null, null, null, null, null, null, null, null,
                null, null,
                "java",
                array, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
        );

        String url = builder.buildUrl(request, baseUrl);

        assertThat(url)
                .contains("keyword=java")
                .contains("keywords[0][srws]=1")
                .contains("keywords[0][skwc]=and")
                .contains("keywords[0][keys]=php")
                .contains("keywords[1][srws]=3")
                .contains("keywords[1][skwc]=particular")
                .contains("keywords[1][keys]=javascript");
    }
}
