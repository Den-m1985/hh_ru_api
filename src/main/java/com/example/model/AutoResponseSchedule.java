package com.example.model;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "auto_response_schedules")
public class AutoResponseSchedule extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    private boolean enabled;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(name = "params_type", nullable = false)
    private String paramsType;

    public <T> Optional <T> getParams(Class<T> clazz) {
        if (clazz.getName().equals(paramsType)) {
            T result = new Gson().fromJson(params, clazz);
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    public void setParams(Object request) {
        this.params = new Gson().toJson(request);
        this.paramsType = request.getClass().getName();
    }

}
