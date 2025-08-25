package com.example.dto.superjob.resume;

import com.example.dto.superjob.Education;
import com.example.dto.superjob.Town;

public record BaseEducationHistory(
        Institute institute,
        Town town,
        String faculty,
        String profession,
        Education education_type,
        String education_form,
        Integer yearend
) {
}
