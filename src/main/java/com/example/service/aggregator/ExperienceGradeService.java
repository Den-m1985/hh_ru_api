package com.example.service.aggregator;

import com.example.dto.agregator_dto.ExperienceGradeRequest;
import com.example.dto.agregator_dto.ExperienceGradeResponse;
import com.example.model.agregator.ExperienceGrade;
import com.example.repository.agregator.ExperienceGradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceGradeService {
    private final ExperienceGradeRepository experienceGradeRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ExperienceGrade getExperienceGradeByName(String experienceName) {
        return experienceGradeRepository.findByName(experienceName)
                .orElseThrow(() -> new EntityNotFoundException("Experience grade with name: " + experienceName + " not found"));
    }

    public ExperienceGrade getExperienceGradeById(Integer experienceGradeId) {
        return experienceGradeRepository.findById(experienceGradeId)
                .orElseThrow(() -> new EntityNotFoundException("Experience grade with id: " + experienceGradeId + " not found"));
    }

    public ExperienceGradeResponse getGradeByNameDto(String name) {
        ExperienceGrade experienceGrade = getExperienceGradeByName(name);
        return mapperGradeToDto(experienceGrade);
    }

    public ExperienceGradeResponse getGradeByIdDto(Integer id) {
        ExperienceGrade experienceGrade = getExperienceGradeById(id);
        return mapperGradeToDto(experienceGrade);
    }

    private ExperienceGradeResponse mapperGradeToDto(ExperienceGrade experienceGrade) {
        return new ExperienceGradeResponse(
                experienceGrade.getId(),
                experienceGrade.getCreatedAt() != null ? experienceGrade.getCreatedAt().format(formatter) : null,
                experienceGrade.getUpdatedAt() != null ? experienceGrade.getCreatedAt().format(formatter) : null,
                experienceGrade.getName(),
                experienceGrade.getDescription()
        );
    }

    public ExperienceGradeResponse addExperienceGrade(ExperienceGradeRequest request) {
        ExperienceGrade experience = new ExperienceGrade();
        experience.setName(request.name());
        experience.setDescription(request.description());
        experience = experienceGradeRepository.save(experience);
        return mapperGradeToDto(experience);
    }

    public List<ExperienceGradeResponse> getAllGrades() {
        List<ExperienceGrade> array = experienceGradeRepository.findAll();
        return array.stream().map(this::mapperGradeToDto).toList();
    }

    public void deleteExperienceGrade(Integer id) {
        ExperienceGrade experience = getExperienceGradeById(id);
        experienceGradeRepository.delete(experience);
    }
}
