package com.example.service.common;

import com.example.RedisTestConfig;
import com.example.dto.AuthResponse;
import com.example.dto.UserDTO;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class RegisterServiceTest {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

    private String email = "john.doe@example.com";
    private String password = "password";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUserAndReturnJwtToken() {
        UserDTO request = new UserDTO(email, password);
        AuthResponse response = registerService.registerUser(request, servletResponse);

        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.userId());

        User registeredUser = userRepository.findByEmail(email).orElse(null);
        assertNotNull(registeredUser);
        assertEquals(email, registeredUser.getEmail());
        assertTrue(passwordEncoder.matches(password, registeredUser.getPassword()));
        assertEquals(RoleEnum.USER, registeredUser.getRole());
    }

    @Test
    void shouldThrowExceptionIfUserAlreadyExists() {
        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setUsername(email);
        existingUser.setPassword(passwordEncoder.encode(password));
        existingUser.setRole(RoleEnum.USER);
        userRepository.save(existingUser);

        UserDTO request = new UserDTO(email, password);

        assertThrows(EntityExistsException.class, () -> registerService.registerUser(request, servletResponse));
    }
}
