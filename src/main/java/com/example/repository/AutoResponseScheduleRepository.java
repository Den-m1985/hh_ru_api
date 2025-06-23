package com.example.repository;

import com.example.model.AutoResponseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutoResponseScheduleRepository extends JpaRepository<AutoResponseSchedule, Integer> {

    Optional<AutoResponseSchedule> findByUserId(Integer userId);
    List<AutoResponseSchedule> findAllByEnabledTrue();
}