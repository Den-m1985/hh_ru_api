package com.example.model.agregator;

import com.example.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "competency_matrix")
public class CompetencyMatrix extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String specialization;

    private String competencies;

    @Column(name = "technical_questions")
    private String technicalQuestions;

}
