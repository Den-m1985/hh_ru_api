package com.example.model.it_map;

import com.example.model.BaseEntity;
import com.example.model.Recruiter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "companies")
public class Company extends BaseEntity {

    @ManyToMany
    @JoinTable(
            name = "company_category_mapping",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CompanyCategory> categories = new HashSet<>();

    @Column(name = "company_name", nullable = false)
    private String name;

    @Column(name = "company_url")
    private String companyUrl;

    @Column(name = "career_url")
    private String careerUrl;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "category_virtual_map")
    private Integer categoryVirtualMap;

    @Column(name = "present_in_virtual_map")
    private Boolean presentInVirtualMap;

    @OneToMany(mappedBy = "company", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Recruiter> recruiter;
}
