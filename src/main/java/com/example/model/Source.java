package com.example.model;

import com.example.enums.SourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Source extends BaseEntity {

    private String name;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Enumerated(EnumType.STRING)
    private SourceType type;

    @Column(name = "search_field")
    private String searchField;

    @Column(name = "array_articles_field")
    private String arrayArticlesField;

    @Column(name = "vacancy_name_field")
    private String vacancyNameField;

    @Column(name = "vacancy_url_field")
    private String vacancyUrlField;
}
