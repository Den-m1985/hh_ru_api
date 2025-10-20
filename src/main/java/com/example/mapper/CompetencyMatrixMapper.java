package com.example.mapper;

import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import com.example.dto.it_map.CompetencyAreasResponse;
import com.example.dto.it_map.CompetencyResponse;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.it_map.Competency;
import com.example.model.it_map.CompetencyAreas;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompetencyMatrixMapper {

    public CompetencyMatrix toEntity() {
        return null;
    }

    public List<CompetencyMatrixResponse> getAllCompetencyMatrixDto(List<CompetencyMatrix> array) {
        return array.stream().map(this::mapperEntityToDto).toList();
    }

    public CompetencyMatrixResponse mapperEntityToDto(CompetencyMatrix competencyMatrix) {
        return new CompetencyMatrixResponse(
                competencyMatrix.getId(),
                competencyMatrix.getSpecialization(),
                competencyMatrix.getCompetencyAreas().get(0).getCompetencies().get(0).getExperienceGrade().getName(),
                mapCompetencyAreasToDto(competencyMatrix.getCompetencyAreas())
        );
    }

    public List<CompetencyAreasResponse> mapCompetencyAreasToDto(List<CompetencyAreas> competencyAreas) {
        List<CompetencyAreasResponse> array = new ArrayList<>();
        for (CompetencyAreas competencyArea : competencyAreas) {
            array.add(new CompetencyAreasResponse(
                    competencyArea.getId(),
                    competencyArea.getArea(),
                    getCompetenciesDto(competencyArea.getCompetencies())
            ));
        }
        return array;
    }

    private List<CompetencyResponse> getCompetenciesDto(List<Competency> competencies) {
        List<CompetencyResponse> array = new ArrayList<>();
        for (Competency competency : competencies) {
            array.add(new CompetencyResponse(
                    competency.getId(),
                    competency.getName(),
                    competency.getAttribute()
            ));
        }
        return array;
    }
}
