package com.example.model.agregator;

import com.example.model.BaseEntity;
import com.example.model.it_map.Competency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "experienceGrade")
public class ExperienceGrade extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "competency_id", unique = true)
    private Competency competency;

    @Column(nullable = false)
    private String name;

    private String description;
}
