package com.example.service.aggregator;

import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import com.example.model.agregator.CompetencyMatrix;
import com.example.repository.agregator.CompetencyMatrixRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetencyMatrixService {
    private final CompetencyMatrixRepository competencyMatrixRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CompetencyMatrix getCompetencyMatrixBySpec(String specialization) {
        return competencyMatrixRepository.findBySpecialization(specialization)
                .orElseThrow(() -> new EntityNotFoundException("Competency matrix with specialization: " + specialization + " not found"));
    }

    public CompetencyMatrix getCompetencyMatrixById(Integer id) {
        return competencyMatrixRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Competency matrix with id: " + id + " not found"));
    }

    public CompetencyMatrixResponse addCompetencyMatrix(CompetencyMatrixRequest request) {
        CompetencyMatrix competency = new CompetencyMatrix();
        competency.setSpecialization(request.specialization());
        competency.setCompetencies(request.competencies());
        competency.setTechnicalQuestions(request.technicalQuestions());
        competency = competencyMatrixRepository.save(competency);
        return mapperEntityToDto(competency);
    }

    public CompetencyMatrixResponse getCompetencyMatrixDto(String specialization) {
        CompetencyMatrix competencyMatrix = getCompetencyMatrixBySpec(specialization);
        return mapperEntityToDto(competencyMatrix);
    }

    public CompetencyMatrixResponse getCompetencyMatrixDto(Integer id) {
        CompetencyMatrix competencyMatrix = getCompetencyMatrixById(id);
        return mapperEntityToDto(competencyMatrix);
    }

    public List<CompetencyMatrixResponse> getAllCompetencyMatrixDto() {
        List<CompetencyMatrix> array = competencyMatrixRepository.findAll();
        return array.stream().map(this::mapperEntityToDto).toList();
    }

    private CompetencyMatrixResponse mapperEntityToDto(CompetencyMatrix competencyMatrix) {
        return new CompetencyMatrixResponse(
                competencyMatrix.getId(),
                competencyMatrix.getCreatedAt() != null ? competencyMatrix.getCreatedAt().format(formatter) : null,
                competencyMatrix.getUpdatedAt() != null ? competencyMatrix.getCreatedAt().format(formatter) : null,
                competencyMatrix.getSpecialization(),
                competencyMatrix.getCompetencies(),
                competencyMatrix.getTechnicalQuestions()
        );
    }

    public void deleteCompetencyMatrix(Integer id) {
        CompetencyMatrix competencyMatrix = getCompetencyMatrixById(id);
        competencyMatrixRepository.delete(competencyMatrix);
    }
}
