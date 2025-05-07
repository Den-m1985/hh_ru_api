package com.example.repository;

import com.example.model.HhToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HhTokenRepository extends JpaRepository<HhToken, String> {
    Optional<HhToken> findByUserId(String userId);
}
