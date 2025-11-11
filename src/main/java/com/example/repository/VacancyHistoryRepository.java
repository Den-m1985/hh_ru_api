package com.example.repository;

import com.example.model.VacancyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacancyHistoryRepository extends JpaRepository<VacancyHistory, Integer> {

    List<VacancyHistory> findByProvider(String provider);


    @Query("SELECT vh FROM VacancyHistory vh WHERE vh.user.id = :userId")
    List<VacancyHistory> getVacancyHistoryByUser(@Param("userId") Integer userId);
}
