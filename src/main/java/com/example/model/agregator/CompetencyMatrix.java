package com.example.model.agregator;

import com.example.model.BaseEntity;
import com.example.model.it_map.CompetencyAreas;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "competency_matrix")
public class CompetencyMatrix extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String specialization;

    @OneToMany(mappedBy = "competencyMatrix", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private List<CompetencyAreas> competencyAreas;
}
