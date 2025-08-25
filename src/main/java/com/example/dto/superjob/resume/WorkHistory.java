package com.example.dto.superjob.resume;

import com.example.dto.superjob.Town;
import com.example.dto.superjob.TypeOfWork;

public record WorkHistory(
        Town town,
        String name,
        String townName,
        String company_website,
        String company_scope,
        String profession,
        String work,
        String achievements,
        TypeOfWork type,
        Integer monthbeg,
        Integer yearbeg,
        Integer monthend,
        Integer yearend
) {
}
