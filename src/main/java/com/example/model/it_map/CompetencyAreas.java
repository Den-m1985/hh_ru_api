package com.example.model.it_map;

import com.example.model.BaseEntity;
import com.example.model.agregator.CompetencyMatrix;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "competency_areas")
public class CompetencyAreas extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "competency_matrix_id")
    private CompetencyMatrix competencyMatrix;

    private String area;

    @OneToMany(mappedBy = "competencyAreas", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Competency> competencies;
}
