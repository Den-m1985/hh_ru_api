package com.example.repository;

import com.example.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {
    Optional<Resume> findByClientId(String clientId);
}
