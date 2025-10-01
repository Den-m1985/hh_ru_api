package com.example.controller;

import com.example.dto.UserDTO;
import com.example.repository.UserRepository;
import com.example.service.common.AuthService;
import com.example.service.common.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegisterService registerService;

    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String email = "john.doe@example.com";
    private final String password = "password";
    private final String endpointRegister = "/v1/users/register";
    private final String endpointLogin = "/v1/users/login";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        UserDTO request = new UserDTO(email, password);
        registerService.registerUser(request, servletResponse);
    }

    @Test
    void createUserTest() throws Exception {
        userRepository.deleteAll();
        UserDTO userDTO = new UserDTO(email, password);
        mockMvc.perform(post(endpointRegister)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() throws Exception {
        UserDTO userDTO = new UserDTO(email, password);
        mockMvc.perform(post(endpointRegister)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.description").value("User already exist"));
    }

    @Test
    void authenticateUserTest() throws Exception {
        UserDTO loginDto = new UserDTO(email, password);
        mockMvc.perform(post(endpointLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {
        UserDTO loginDto = new UserDTO(email, "wrongPassword");

        mockMvc.perform(post(endpointLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.description").value("The username or password is incorrect"));
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsMissing() throws Exception {
        UserDTO loginDto = new UserDTO(null, password);

        mockMvc.perform(post(endpointLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(jsonPath("$.description").value("Validation failed for one or more fields"));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsMissing() throws Exception {
        UserDTO loginDto = new UserDTO(email, null);

        mockMvc.perform(post(endpointLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(jsonPath("$.description").value("Validation failed for one or more fields"));
    }

    @Test
    void shouldReturnBadRequestForInvalidEmailFormat() throws Exception {
        UserDTO loginDto = new UserDTO("not-an-email", "password");

        mockMvc.perform(post(endpointLogin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(jsonPath("$.description").value("Validation failed for one or more fields"));
    }

}
