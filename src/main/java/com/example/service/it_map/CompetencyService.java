package com.example.service.it_map;

import com.example.dto.it_map.CompetencyRequest;
import com.example.model.agregator.ExperienceGrade;
import com.example.model.it_map.Competency;
import com.example.model.it_map.CompetencyAreas;
import com.example.repository.it_map.CompetencyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompetencyService {
    CompetencyRepository competencyRepository;
    ExperienceGradeService experienceGradeService;

    @Transactional
    public List<Competency> getCompetencyList(List<CompetencyRequest> competencies) {
        List<Competency> competencyList = new ArrayList<>();
        for (CompetencyRequest request : competencies) {
            Competency competency = getOrCreateCompetencyArea(
                    request.id(),
                    request.name(),
                    null,
                    experienceGradeService.getOrCreateGrade(request.experienceGradeId(), request.experienceGradeName(), ""),
                    request.attribute()
            );
            competencyList.add(competency);
        }
        return competencyList;
    }

    public Competency getOrCreateCompetencyArea(Integer id, String name, CompetencyAreas competencyAreas, ExperienceGrade experienceGradeJunior, List<String> attribute) {
        if (id == null && (name == null || name.isBlank())) {
            throw new IllegalArgumentException("Area is required for creation CompetencyAreas");
        }
        if (id != null) {
            Optional<Competency> optional = competencyRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return competencyRepository.findByName(name)
                .orElseGet(() -> createNewEntity(name, competencyAreas, experienceGradeJunior, attribute));
    }

    private Competency createNewEntity(String name, CompetencyAreas competencyAreas, ExperienceGrade experienceGradeJunior, List<String> attribute) {
        Competency competencySemantic = new Competency();
        competencySemantic.setCompetencyAreas(competencyAreas);
        competencySemantic.setName(name);
        competencySemantic.setExperienceGrade(experienceGradeJunior);
        competencySemantic.setAttribute(attribute);
        return save(competencySemantic);
    }

    public Competency save(Competency competency) {
        return competencyRepository.save(competency);
    }
}
