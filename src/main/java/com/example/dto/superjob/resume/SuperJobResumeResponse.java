package com.example.dto.superjob.resume;

import java.util.List;

public record SuperJobResumeResponse(
        List<ResumeObject> objects,
        Boolean more,
        Integer total
) {
}
