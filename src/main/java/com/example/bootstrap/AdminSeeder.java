package com.example.bootstrap;

import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.common.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Value("${supper_admin.email}")
    private String supperAdminEmail;
    @Value("${supper_admin.password}")
    private String supperAdminPassword;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        User user;
        try {
            userService.getUserByEmail(supperAdminEmail);
        } catch (EntityNotFoundException e) {
            user = new User();
            user.setFirstName("Super Admin");
            user.setEmail(supperAdminEmail);
            user.setUsername(supperAdminEmail);
            user.setPassword(passwordEncoder.encode(supperAdminPassword));
            user.setRole(RoleEnum.SUPER_ADMIN);
            user.setPhone("+71234567890");
            userRepository.save(user);
        }
        createTestUser();
    }

    private void createTestUser() {
        User user;
        String email = "test@test.ru";
        try {
            userService.getUserByEmail(email);
        } catch (EntityNotFoundException e) {
            user = new User();
            user.setFirstName("user test");
            user.setEmail(email);
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(RoleEnum.USER);
            user.setPhone("+71234567891");
            userRepository.save(user);
        }
    }
}
