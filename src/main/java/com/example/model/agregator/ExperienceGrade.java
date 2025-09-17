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
@Table(name = "experienceGrade")
public class ExperienceGrade extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;
}
