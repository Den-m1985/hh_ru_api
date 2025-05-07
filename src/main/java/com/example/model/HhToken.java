package com.example.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class HhToken extends BaseEntity {


    private String userId;

    private String accessToken;

    private Long expiresIn;

    private String refreshToken;

    private String token_type;
}
