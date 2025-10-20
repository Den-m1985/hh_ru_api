package com.example.repository.it_map;

import com.example.model.agregator.ExperienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExperienceGradeRepository extends JpaRepository<ExperienceGrade, Integer> {
    Optional<ExperienceGrade> findByName(String name);

}
