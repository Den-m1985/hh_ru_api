package com.example.repository;

import com.example.model.SuperjobToken;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperjobTokenRepository extends JpaRepository<SuperjobToken, Integer> {
    Optional<SuperjobToken> findSuperjobTokenByUser(User user);
}
