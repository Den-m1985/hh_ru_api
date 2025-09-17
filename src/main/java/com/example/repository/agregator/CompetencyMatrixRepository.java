package com.example.repository.agregator;

import com.example.model.agregator.CompetencyMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetencyMatrixRepository extends JpaRepository<CompetencyMatrix, Integer> {
    Optional<CompetencyMatrix> findBySpecialization(String specialization);
}
