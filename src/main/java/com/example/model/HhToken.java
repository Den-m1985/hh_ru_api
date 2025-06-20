package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class HhToken extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "access_token")
    private String accessToken;

    // life in sec
    @Column(name = "expires_in")
    private Long expiresIn;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_type")
    private String tokenType;
}
