package com.example.service.mapper;

import com.example.dto.VacancyRequest;
import lombok.experimental.UtilityClass;

import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class VacancyRequestMapper {

    public Map<String, String> toParamMap(VacancyRequest req, int page, int perPage) {
        Map<String, String> params = new LinkedHashMap<>();

        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));

        // добавляем только если не null
        put(params, "text", req.text());
        put(params, "search_field", req.search_field());
        put(params, "experience", req.experience());
        put(params, "employment", req.employment());
        put(params, "schedule", req.schedule());
        put(params, "area", req.area());
        put(params, "metro", req.metro());
        put(params, "professional_role", req.professional_role());
        put(params, "industry", req.industry());
        put(params, "employer_id", req.employer_id());
        put(params, "currency", req.currency());
        put(params, "salary", req.salary() == null ? null : String.valueOf(req.salary()));
        put(params, "label", req.label());
        put(params, "only_with_salary", toStr(req.only_with_salary()));
        put(params, "period", toStr(req.period()));
        put(params, "date_from", req.date_from());
        put(params, "date_to", req.date_to());
        put(params, "top_lat", toStr(req.top_lat()));
        put(params, "bottom_lat", toStr(req.bottom_lat()));
        put(params, "left_lng", toStr(req.left_lng()));
        put(params, "right_lng", toStr(req.right_lng()));
        put(params, "order_by", req.order_by());
        put(params, "sort_point_lat", toStr(req.sort_point_lat()));
        put(params, "sort_point_lng", toStr(req.sort_point_lng()));
        put(params, "clusters", toStr(req.clusters()));
        put(params, "describe_arguments", toStr(req.describe_arguments()));
        put(params, "no_magic", toStr(req.no_magic()));
        put(params, "premium", toStr(req.premium()));
        put(params, "responses_count_enabled", toStr(req.responses_count_enabled()));
        put(params, "part_time", req.part_time());
        put(params, "accept_temporary", toStr(req.accept_temporary()));
        put(params, "locale", req.locale());
        put(params, "host", req.host());

        return params;
    }

    private void put(Map<String, String> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }

    private String toStr(Object value) {
        return value == null ? null : value.toString();
    }
}
