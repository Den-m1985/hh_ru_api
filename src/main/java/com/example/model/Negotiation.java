package com.example.model;

import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "negotiations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Negotiation extends BaseEntity {

    @Enumerated(EnumType.STRING)
    NegotiationState state;

    @Enumerated(EnumType.STRING)
    ApiProvider provider;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_negotiations_user_id")
    )
    User user;

    String externalId;

    String statusText;

    String comment;

    String vacancyUrl;

    String companyName;

    String positionName;

    Boolean viewedByOpponent;

    LocalDateTime sendAt;
}
