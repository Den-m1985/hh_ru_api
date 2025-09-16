package com.example.service.aggregator;

import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.model.agregator.CompetencyMatrix;
import com.example.repository.agregator.CompetencyMatrixRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetencyMatrixService {
    private final CompetencyMatrixRepository competencyMatrix;

    public CompetencyMatrix getCompetencyMatrix(String specialization) {
        return competencyMatrix.findBySpecialization(specialization)
                .orElseThrow(() -> new EntityNotFoundException("Competency matrix with specialization: " + specialization + " not found"));
    }

    public CompetencyMatrix addCompetencyMatrix(CompetencyMatrixRequest request) {
        CompetencyMatrix competency = new CompetencyMatrix();
        competency.setSpecialization(request.specialization());
        competency.setCompetencies(request.competencies());
        competency.setTechnicalQuestions(request.technicalQuestions());
        return competencyMatrix.save(competency);
    }
}
