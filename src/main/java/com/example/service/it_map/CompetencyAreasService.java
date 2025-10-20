package com.example.service.it_map;

import com.example.dto.it_map.CompetencyAreasRequest;
import com.example.dto.it_map.CompetencyAreasResponse;
import com.example.mapper.CompetencyMatrixMapper;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.it_map.Competency;
import com.example.model.it_map.CompetencyAreas;
import com.example.repository.it_map.CompetencyAreasRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class CompetencyAreasService {
    CompetencyAreasRepository competencyAreasRepository;
    CompetencyMatrixMapper competencyMatrixMapper;
    CompetencyService competencyService;

    public CompetencyAreas getCompetencyAreasById(Integer id) {
        return competencyAreasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Competency area with id: " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<CompetencyAreasResponse> getAllCompetencyAreasDto() {
        List<CompetencyAreas> array = competencyAreasRepository.findAll();
        return competencyMatrixMapper.mapCompetencyAreasToDto(array);
    }

    @Transactional
    public List<CompetencyAreas> getListAreas(List<CompetencyAreasRequest> competencies) {
        List<CompetencyAreas> getListAreas = new ArrayList<>();
        for (CompetencyAreasRequest request:competencies){
            CompetencyAreas competencyAreas = getOrCreateCompetencyArea(
                    request.id(),
                    request.area(),
                    null,
                    competencyService.getCompetencyList(request.competencies())
            );
            getListAreas.add(competencyAreas);
        }
        return getListAreas;
    }

    public CompetencyAreas getOrCreateCompetencyArea(Integer id, String area, CompetencyMatrix competencyMatrix, List<Competency> competencies) {
        if (id == null && (area == null || area.isBlank())) {
            throw new IllegalArgumentException("Area is required for creation CompetencyAreas");
        }
        if (id != null) {
            Optional<CompetencyAreas> optional = competencyAreasRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return competencyAreasRepository.findByArea(area)
                .orElseGet(() -> createNewEntity(area, competencyMatrix, competencies));
    }

    private CompetencyAreas createNewEntity(String area, CompetencyMatrix competencyMatrix, List<Competency> competencies) {
        CompetencyAreas competencyAreas = new CompetencyAreas();
        competencyAreas.setCompetencyMatrix(competencyMatrix);
        competencyAreas.setArea(area);
        competencyAreas.setCompetencies(competencies);
        return competencyAreasRepository.save(competencyAreas);
    }

    public CompetencyAreas save(CompetencyAreas competencyAreas) {
        return competencyAreasRepository.save(competencyAreas);
    }

    public void deleteCompetencyArea(Integer id) {
        CompetencyAreas competencyMatrix = getCompetencyAreasById(id);
        competencyAreasRepository.delete(competencyMatrix);
    }
}
