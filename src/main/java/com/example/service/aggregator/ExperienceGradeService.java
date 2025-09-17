package com.example.service.aggregator;

import com.example.dto.agregator_dto.ExperienceGradeRequest;
import com.example.model.agregator.ExperienceGrade;
import com.example.repository.agregator.ExperienceGradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceGradeService {
    private final ExperienceGradeRepository experienceGrade;

    public ExperienceGrade getExperienceGradeByName(String experienceName) {
        return experienceGrade.findByName(experienceName)
                .orElseThrow(() -> new EntityNotFoundException("Experience grade with name: " + experienceName + " not found"));
    }

    public ExperienceGrade getExperienceGradeById(Integer experienceGradeId) {
        return experienceGrade.findById(experienceGradeId)
                .orElseThrow(() -> new EntityNotFoundException("Experience grade with id: " + experienceGradeId + " not found"));
    }

    public ExperienceGrade addExperienceGrade(ExperienceGradeRequest request) {
        ExperienceGrade experience = new ExperienceGrade();
        experience.setName(request.name());
        experience.setDescription(request.description());
        return experienceGrade.save(experience);
    }
}
