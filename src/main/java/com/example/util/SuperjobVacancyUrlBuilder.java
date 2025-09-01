package com.example.util;

import com.example.dto.superjob.SuperjobSearchRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@Component
public class SuperjobVacancyUrlBuilder {

    public String buildUrl(SuperjobSearchRequest request, String baseUrl) {
        UriComponentsBuilder builder = baseBuilder(baseUrl);
        addQueryParams(builder, request);
        return builder.build(false).toUriString();
    }

    private UriComponentsBuilder baseBuilder(String baseUrl) {
        return UriComponentsBuilder.fromUriString(baseUrl + "/2.0/vacancies/");
    }

    private void addQueryParams(UriComponentsBuilder builder, SuperjobSearchRequest request) {
        addParam(builder, "id_client", request.id_client());
        addParam(builder, "id_user", request.id_user());
        addParam(builder, "id_resume", request.id_resume());
        addParam(builder, "id_subs", request.id_subs());
        addParam(builder, "date_published_from", request.date_published_from());
        addParam(builder, "date_published_to", request.date_published_to());
        addParam(builder, "sort_new", request.sort_new());
        addParam(builder, "published", request.published());
        addParam(builder, "published_all", request.published_all());
        addParam(builder, "archive", request.archive());
        addParam(builder, "not_archive", request.not_archive());
        addParam(builder, "keyword", request.keyword());
        addParam(builder, "order_field", request.order_field());
        addParam(builder, "order_direction", request.order_direction());
        addParam(builder, "period", request.period());
        addParam(builder, "payment_from", request.payment_from());
        addParam(builder, "payment_to", request.payment_to());
        addParam(builder, "no_agreement", request.no_agreement());
        addParam(builder, "town", request.town());
        addParam(builder, "place_of_work", request.place_of_work());
        addParam(builder, "moveable", request.moveable());
        addParam(builder, "agency", request.agency());
        addParam(builder, "type_of_work", request.type_of_work());
        addParam(builder, "age", request.age());
        addParam(builder, "gender", request.gender());
        addParam(builder, "education", request.education());
        addParam(builder, "experience", request.experience());
        addParam(builder, "driving_particular", request.driving_particular());
        addParam(builder, "language", request.language());
        addParam(builder, "lang_level", request.lang_level());
        addParam(builder, "languages_particular", request.languages_particular());
        addParam(builder, "nolang", request.nolang());

        if (request.catalogues() != null && !request.catalogues().isEmpty()) {
            addParam(builder, "catalogues",
                    request.catalogues().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")));
        }

        if (request.driving_licence() != null) {
            for (int i = 0; i < request.driving_licence().size(); i++) {
                addParam(builder, "driving_licence[" + i + "]", request.driving_licence().get(i));
            }
        }

        if (request.keywords() != null) {
            String keywords = "keywords";
            for (int i = 0; i < request.keywords().size(); i++) {
                var kw = request.keywords().get(i);
                addParam(builder, keywords + "[" + i + "][srws]", kw.srws());
                addParam(builder, keywords + "[" + i + "][skwc]", kw.skwc());
                addParam(builder, keywords + "[" + i + "][keys]", kw.keys());
            }
        }
    }

    private void addParam(UriComponentsBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }
}
