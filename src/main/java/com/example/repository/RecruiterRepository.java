package com.example.repository;

import com.example.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

    List<Recruiter> getRecruitersByCompanyId(Integer companyId);
}
