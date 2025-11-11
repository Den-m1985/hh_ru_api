package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "vacancy_responses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VacancyHistory extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    String provider;

    String vacancyUrl;
}
