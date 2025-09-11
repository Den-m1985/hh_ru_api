package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(name = "companies")
public class Company extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CompanyCategory category;

    @Column(name = "company_name", nullable = false)
    private String name;

    @Column(name = "company_url")
    private String companyUrl;

    @Column(name = "career_url")
    private String careerUrl;

    @OneToMany(mappedBy = "company", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Recruiter> recruiter;
}
