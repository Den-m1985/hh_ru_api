package com.example.repository.it_map;

import com.example.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> findCompanyByName(String name);
    Optional<Company> findCompanyById(Integer id);

    @Query("SELECT c FROM Company c WHERE c.category.name IN :categories")
    List<Company> findByCategoryNames(@Param("categories") List<String> categories);
}
