package com.example.repository;

import com.example.model.HhToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HhTokenRepository extends JpaRepository<HhToken, String> {
}
