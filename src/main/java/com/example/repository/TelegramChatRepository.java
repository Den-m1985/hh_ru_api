package com.example.repository;

import com.example.model.TelegramChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramChatRepository extends JpaRepository<TelegramChat, Integer> {

    Optional<TelegramChat> findByUserId(Integer userId);

    Optional<TelegramChat> findByTelegramUserId(Long telegramUserId);
}
