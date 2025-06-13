package com.example.repository;

import com.example.model.Resume;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {

    Optional<Resume> findByHhResumeId(String hhResumeId);

    Optional<Resume> findResumeByUser(User user);
}
