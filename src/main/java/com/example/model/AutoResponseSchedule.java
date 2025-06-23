package com.example.model;

import com.example.dto.VacancyRequest;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "auto_response_schedules")
public class AutoResponseSchedule extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private boolean enabled;

    @Column(columnDefinition = "TEXT")
    private String params;

    public VacancyRequest getParams() {
        return new Gson().fromJson(params, VacancyRequest.class);
    }

    public void setParams(VacancyRequest request) {
        this.params = new Gson().toJson(request);
    }
}
