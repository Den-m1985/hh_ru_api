package com.example.repository;

import com.example.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

    @Query("SELECT r FROM Recruiter r WHERE r.company.id = :companyId")
    List<Recruiter> getRecruitersByCompanyId(@Param("companyId") Integer companyId);

}
