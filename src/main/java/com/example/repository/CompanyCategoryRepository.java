package com.example.repository;

import com.example.model.CompanyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyCategoryRepository extends JpaRepository<CompanyCategory, Integer> {
}
