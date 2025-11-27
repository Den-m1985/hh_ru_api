package com.example.repository;

import com.example.enums.ApiProvider;
import com.example.model.Negotiation;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NegotiationRepository extends JpaRepository<Negotiation, Long> {
    List<Negotiation> findAllByUserAndProvider(User user, ApiProvider provider);
    List<Negotiation> findAllByUser(User user);
}
