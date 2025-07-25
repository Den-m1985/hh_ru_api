package com.example.repository;


import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT r.user.id FROM Resume r WHERE r.resumeId = :resumeId")
    Optional<Integer> findUserIdByResumeId(@Param("resumeId") String resumeId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByTelegramUserId(Long telegramUserId);
}
