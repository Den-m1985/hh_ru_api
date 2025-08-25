package com.example.dto.superjob;

import com.example.dto.interfaces.BaseResumeDto;

public record SuperjobResumeDto(

        String title,
        Integer id

) implements BaseResumeDto {
}
