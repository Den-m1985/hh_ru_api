package com.example.repository.it_map;

import com.example.model.it_map.CompetencyAreas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetencyAreasRepository extends JpaRepository<CompetencyAreas, Integer> {

    Optional<CompetencyAreas> findByArea(String area);
}
