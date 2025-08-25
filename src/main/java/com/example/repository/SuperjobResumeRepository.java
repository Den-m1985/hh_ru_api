package com.example.repository;

import com.example.model.SuperjobResume;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperjobResumeRepository extends JpaRepository<SuperjobResume, Integer> {
    Optional<SuperjobResume> findSuperjobResumeByUser(User user);
}
