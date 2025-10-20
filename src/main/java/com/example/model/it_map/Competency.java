package com.example.model.it_map;

import com.example.model.BaseEntity;
import com.example.model.agregator.ExperienceGrade;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "competency")
public class Competency  extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "competency_areas_id")
    private CompetencyAreas competencyAreas;

    private String name;

    @OneToOne(mappedBy = "competency", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    @JoinColumn(name = "experience_grade_id")
    private ExperienceGrade experienceGrade;

    @ElementCollection // Создаст таблицу competencies_attributes
    @CollectionTable(name = "competency_attributes", joinColumns = @JoinColumn(name = "competency_id"))
    @Column(name = "attribute_value")
    private List<String> attribute;
}
