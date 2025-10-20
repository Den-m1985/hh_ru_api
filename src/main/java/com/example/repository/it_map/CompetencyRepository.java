package com.example.repository.it_map;

import com.example.model.it_map.Competency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetencyRepository extends JpaRepository<Competency, Integer> {
    Optional<Competency> findByName(String name);
}
