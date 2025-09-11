package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "company_categories")
public class CompanyCategory extends BaseEntity {

    /*
     BigTech
    Food-Tech
    Telecom
    Fin-Tech
    Travel-Tech
    Аутсорс / Аутстафф / Интеграторы
    E-com
    Ed-Tech
    Кибербез
    Медиа
    Госкомпании
     */
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();
}
