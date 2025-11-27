package com.example.model;

import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "negotiations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Negotiation extends BaseEntity {

    String externalId;

    @Enumerated(EnumType.STRING)
    NegotiationState state;

    String statusText;

    Boolean viewedByOpponent;

    @Enumerated(EnumType.STRING)
    ApiProvider provider;

    LocalDateTime sendAt;

    String comment;

    String vacancyUrl;

    String companyName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    String positionName;
}
