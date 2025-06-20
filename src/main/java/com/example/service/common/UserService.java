package com.example.service.common;

import com.example.dto.UserInfoDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
    }

    public User getUserByEmail(String userEmail) {
        return findUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with mail: " + userEmail + " not found"));
    }

    public Optional<User> findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    public User findUserByResume(String resumeId) {
        Integer userId = findUserIdByResume(resumeId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User details not found"));
    }

    private Integer findUserIdByResume(String resumeId) {
        return userRepository.findUserIdByResumeId(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("User with resume Id: " + resumeId + " not found"));
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserInfoDto getUserInfo(String email) {
        User user = getUserByEmail(email);
        return new UserInfoDto(
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender(),
                user.getRole().name()
        );
    }
}
