package com.example.repository.it_map;

import com.example.model.it_map.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> findCompanyByName(String name);
    Optional<Company> findCompanyById(Integer id);

    @Query("SELECT DISTINCT c FROM Company c " +
            "JOIN FETCH c.categories cat " +
            "LEFT JOIN FETCH c.recruiter r " +
            "WHERE cat.name IN :categories")
    List<Company> findByCategoryNames(@Param("categories") List<String> categories);

    @Query("SELECT DISTINCT c FROM Company c " +
            "JOIN c.categories cat " +
            "WHERE cat.id IN :categoryIds")
    List<Company> findByCategoryIds(@Param("categoryIds") List<Integer> categoryIds);
}
