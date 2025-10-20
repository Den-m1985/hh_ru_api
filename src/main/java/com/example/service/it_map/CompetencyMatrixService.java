package com.example.service.it_map;

import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import com.example.dto.it_map.CompetencyAreasResponse;
import com.example.dto.it_map.CompetencyMatrixFilterRequest;
import com.example.mapper.CompetencyMatrixMapper;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.it_map.CompetencyAreas;
import com.example.repository.it_map.CompetencyMatrixRepository;
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
public class CompetencyMatrixService {
    CompetencyMatrixRepository competencyMatrixRepository;
    CompetencyAreasService competencyAreasService;
    CompetencyMatrixMapper competencyMatrixMapper;

    public CompetencyMatrix getCompetencyMatrixBySpec(String specialization) {
        return competencyMatrixRepository.findBySpecialization(specialization)
                .orElseThrow(() -> new EntityNotFoundException("Competency matrix with specialization: " + specialization + " not found"));
    }

    public CompetencyMatrix getCompetencyMatrixById(Integer id) {
        return competencyMatrixRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Competency matrix with id: " + id + " not found"));
    }

    @Transactional
    public CompetencyMatrixResponse returnCompetencyMatrixDto(CompetencyMatrixRequest request) {
        CompetencyMatrix competencyMatrix = addCompetencyMatrix(request);
        return competencyMatrixMapper.mapperEntityToDto(competencyMatrix);
    }

    public CompetencyMatrix addCompetencyMatrix(CompetencyMatrixRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        CompetencyMatrix competencyMatrix = getCompetencyMatrixFromDB(request.id(), request.specialization());
        List<CompetencyAreas> competencyAreas = competencyAreasService.getListAreas(request.competencies());
        competencyMatrix.getCompetencyAreas().addAll(competencyAreas);
        competencyAreas.forEach(area -> area.setCompetencyMatrix(competencyMatrix));
        competencyMatrixRepository.save(competencyMatrix);
        return competencyMatrix;
    }

    private CompetencyMatrix getCompetencyMatrixFromDB(Integer id, String specialization) {
        if (id == null && (specialization == null || specialization.isBlank())) {
            throw new IllegalArgumentException("Name is required for creation ExperienceGrade");
        }
        Optional<CompetencyMatrix> optionalMatrix;
        if (id != null) {
            optionalMatrix = competencyMatrixRepository.findById(id);
        } else {
            optionalMatrix = competencyMatrixRepository.findBySpecialization(specialization);
        }
        return optionalMatrix.orElseGet(() -> createNewEntity(specialization, new ArrayList<>()));
    }

    private CompetencyMatrix createNewEntity(String specialization, List<CompetencyAreas> competencyAreas) {
        CompetencyMatrix competencyMatrix = new CompetencyMatrix();
        competencyMatrix.setSpecialization(specialization);
        competencyMatrix.setCompetencyAreas(competencyAreas);
        return competencyMatrix;
    }

    public List<CompetencyAreasResponse> getCompetencyMatrixFilteredDto(CompetencyMatrixFilterRequest request) {
        CompetencyMatrix competencyMatrix =
                competencyMatrixRepository.findFilteredMatrix(request.specialization(), request.experienceGrade());
        if (competencyMatrix == null) {
            String message = "Competency matrix with specialization: "
                    + request.specialization()
                    + " and grade: " + request.experienceGrade()
                    + " not found";
            throw new EntityNotFoundException(message);
        }
        return competencyMatrixMapper.mapCompetencyAreasToDto(competencyMatrix.getCompetencyAreas());
    }

    @Transactional(readOnly = true)
    public CompetencyMatrixResponse getCompetencyMatrixDto(Integer id) {
        CompetencyMatrix competencyMatrix = getCompetencyMatrixById(id);
        return competencyMatrixMapper.mapperEntityToDto(competencyMatrix);
    }

    @Transactional(readOnly = true)
    public List<CompetencyMatrixResponse> getAllCompetencyMatrixDto() {
        List<CompetencyMatrix> array = competencyMatrixRepository.findAll();
        return competencyMatrixMapper.getAllCompetencyMatrixDto(array);
    }

    public void deleteCompetencyMatrix(Integer id) {
        CompetencyMatrix competencyMatrix = getCompetencyMatrixById(id);
        competencyMatrixRepository.delete(competencyMatrix);
    }
}
