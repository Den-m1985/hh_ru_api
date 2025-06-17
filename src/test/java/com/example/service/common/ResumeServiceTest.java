package com.example.service.common;

import com.example.dto.AuthResponse;
import com.example.dto.UserDTO;
import com.example.dto.vacancy_dto.ResumeDto;
import com.example.dto.vacancy_dto.ResumeItemDto;
import com.example.model.AuthUser;
import com.example.model.HhToken;
import com.example.model.Resume;
import com.example.model.User;
import com.example.repository.ResumeRepository;
import com.example.repository.UserRepository;
import com.example.service.HhTokenService;
import com.example.util.RequestTemplates;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
class ResumeServiceTest {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private ResumeRepository resumeRepository;
    @MockitoBean
    private RequestTemplates requestTemplates;
    @Autowired
    private HhTokenService hhTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResumeService resumeService;
    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

    private AuthUser testAuthUser;
    private User testUser;

    @BeforeEach
    void setUp() {
        resumeRepository.deleteAll();
        userRepository.deleteAll();

        String email = "john.doe@example.com";
        String password = "password";
        UserDTO request = new UserDTO(email, password);
        AuthResponse response = registerService.registerUser(request, servletResponse);
        assertNotNull(response.userId());
        testUser = userRepository.findByEmail(email).orElse(null);

        HhToken hhToken = new HhToken();
        hhToken.setAccessToken("test_access_token");
        hhToken.setExpiresIn(10000L);
        hhToken.setUser(testUser);
        hhTokenService.saveToken(hhToken);
        assertNotNull(testUser);
        testUser.setHhToken(hhToken);

        testAuthUser = new AuthUser(testUser);
    }

    @Test
    void testGetResumesFromHh_shouldSaveNewResumes() {
        ResumeItemDto dto1 = new ResumeItemDto("Resume 1", UUID.randomUUID().toString());
        ResumeItemDto dto2 = new ResumeItemDto("Resume 2", UUID.randomUUID().toString());
        ResumeDto resumeDto = new ResumeDto(2, 1, 1, 2, List.of(dto1, dto2));
        Mockito.when(requestTemplates.getMineResume(Mockito.any())).thenReturn(resumeDto);

        List<ResumeItemDto> result = resumeService.getResumeItemDto(testAuthUser);

        assertThat(result).hasSize(2);
        List<Resume> savedResumes = resumeRepository.findAll();
        assertThat(savedResumes).hasSize(2);
        assertThat(savedResumes).extracting(Resume::getResumeId)
                .containsExactlyInAnyOrder(dto1.id(), dto2.id());
    }

    @Test
    @Transactional
    void testGetResumesFromHh_shouldNotDuplicateExistingResumes() {
        String resumeId = UUID.randomUUID().toString();
        Resume existing = new Resume();
        existing.setResumeId(resumeId);
        existing.setResumeTitle("title");
        existing.setUser(testUser);
        resumeRepository.save(existing);
        List<Resume> resumeList = new ArrayList<>();
        resumeList.add(existing);
        testUser.setResume(resumeList);

        ResumeItemDto duplicateDto = new ResumeItemDto("Resume Existing", resumeId);
        ResumeItemDto newDto = new ResumeItemDto("Resume New", UUID.randomUUID().toString());
        ResumeDto resumeDto = new ResumeDto(2, 1, 1, 2, List.of(duplicateDto, newDto));
        Mockito.when(requestTemplates.getMineResume(Mockito.any())).thenReturn(resumeDto);

        List<ResumeItemDto> result = resumeService.getResumeItemDto(testAuthUser);

        assertThat(result).hasSize(2);
        List<Resume> saved = resumeRepository.findAll();
        assertThat(saved).hasSize(2);
    }

    @Test
    @Transactional
    void testGetResumesFromHh_withEmptyResponse_shouldReturnEmptyList() {
        ResumeDto resumeDto = new ResumeDto(0, 0, 0, 0, List.of());
        Mockito.when(requestTemplates.getMineResume(Mockito.any())).thenReturn(resumeDto);

        List<ResumeItemDto> result = resumeService.getResumeItemDto(testAuthUser);

        assertThat(result).isEmpty();
        assertThat(resumeRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    void testGetResumesFromHh_withEmptyResponse() {
        Resume existing = new Resume();
        existing.setResumeId("id");
        existing.setResumeTitle("title");
        existing.setUser(testUser);
        resumeRepository.save(existing);
        testUser.setResume(new ArrayList<>(List.of(existing)));
        testAuthUser = new AuthUser(testUser);

        ResumeDto resumeDto = new ResumeDto(0, 0, 0, 0, List.of());
        Mockito.when(requestTemplates.getMineResume(Mockito.any())).thenReturn(resumeDto);

        List<ResumeItemDto> result = resumeService.getResumeItemDto(testAuthUser);

        assertThat(result).isEmpty();
        assertThat(resumeRepository.findAll()).isEmpty();
    }

}
