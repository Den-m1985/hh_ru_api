package com.example.repository;

import com.example.enums.ApiProvider;
import com.example.model.Negotiation;
import com.example.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NegotiationRepository extends JpaRepository<Negotiation, Integer> {
    List<Negotiation> findAllByUserAndProvider(User user, ApiProvider provider);
    List<Negotiation> findAllByUser(User user);
    List<Negotiation> findAllByUserOrderBySendAtDesc(User user, Pageable pageable);

    Optional<Negotiation> findByIdAndUser(Integer id, User user);
}
