package com.example.repository;

import com.example.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

    @Query("SELECT r FROM Recruiter r WHERE r.company.id = :companyId")
    List<Recruiter> getRecruitersByCompanyId(@Param("companyId") Integer companyId);

    @Query("SELECT r FROM Recruiter r WHERE r.email = :email")
    Optional<Recruiter> getRecruiterByEmail(@Param("email") String email);

    @Query("SELECT r FROM Recruiter r WHERE r.contactTelegram = :contactTelegram")
    Optional<Recruiter> getRecruitersByTelegram(@Param("contactTelegram") String contactTelegram);

    @Query("SELECT r FROM Recruiter r WHERE r.contactLinkedIn = :contactLinkedIn")
    Optional<Recruiter> getRecruitersByLinkedIn(@Param("contactLinkedIn") String contactLinkedIn);

}
