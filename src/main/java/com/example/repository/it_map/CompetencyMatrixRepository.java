package com.example.repository.it_map;

import com.example.model.agregator.CompetencyMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompetencyMatrixRepository extends JpaRepository<CompetencyMatrix, Integer> {
    Optional<CompetencyMatrix> findBySpecialization(String specialization);

    @Query("SELECT DISTINCT cm FROM CompetencyMatrix cm " +
            "JOIN FETCH cm.competencyAreas areas " +
            "JOIN areas.competencies comps " +
            "JOIN comps.experienceGrade grade " +
            "WHERE (:specialization IS NULL OR cm.specialization = :specialization) " +
            "AND (:experienceGrade IS NULL OR grade.name = :experienceGrade)")
    CompetencyMatrix findFilteredMatrix(
            @Param("specialization") String specialization,
            @Param("experienceGrade") String experienceGrade
    );
}
