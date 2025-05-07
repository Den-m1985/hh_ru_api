package com.example.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Resume extends BaseEntity{
    private String clientId;
    private String resumeId;
}
