package com.example.model.it_map;

import com.example.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy = "categories")
    private Set<Company> companies = new HashSet<>();
}
