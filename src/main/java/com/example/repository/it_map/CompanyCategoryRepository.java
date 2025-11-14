package com.example.repository.it_map;

import com.example.model.it_map.CompanyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyCategoryRepository extends JpaRepository<CompanyCategory, Integer> {

    Optional<CompanyCategory> findByName(String category);

    List<CompanyCategory> findByNameIn(List<String> names);
}
