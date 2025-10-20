package com.example.service.it_map;

import com.example.dto.agregator_dto.ExperienceGradeRequest;
import com.example.dto.agregator_dto.ExperienceGradeResponse;
import com.example.model.agregator.ExperienceGrade;
import com.example.repository.it_map.ExperienceGradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExperienceGradeService {
    ExperienceGradeRepository experienceGradeRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ExperienceGrade getExperienceGradeByName(String experienceName) {
        return experienceGradeRepository.findByName(experienceName)
                .orElseThrow(() -> new EntityNotFoundException("Experience grade with name: " + experienceName + " not found"));
    }

    public ExperienceGrade getExperienceGradeById(Integer experienceGradeId) {
        return experienceGradeRepository.findById(experienceGradeId)
                .orElseThrow(() -> new EntityNotFoundException("Experience grade with id: " + experienceGradeId + " not found"));
    }

    @Transactional
    public ExperienceGrade getOrCreateGrade(Integer id, String name, String description) {
        if (id == null && (name == null || name.isBlank())) {
            throw new IllegalArgumentException("Name is required for creation ExperienceGrade");
        }
        Optional<ExperienceGrade> optional;
        if (id != null) {
            optional = experienceGradeRepository.findById(id);
        } else {
            optional = experienceGradeRepository.findByName(name);
        }
        return optional.orElseGet(() -> createNewGrade(name, description));
    }

    private ExperienceGrade createNewGrade(String name, String description) {
        ExperienceGrade grade = new ExperienceGrade();
        grade.setName(name);
        grade.setDescription(description);
        return experienceGradeRepository.save(grade);
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
        experience = save(experience);
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

    public ExperienceGrade save(ExperienceGrade experienceGrade) {
        return experienceGradeRepository.save(experienceGrade);
    }
}
