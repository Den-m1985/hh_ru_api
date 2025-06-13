package com.example.service.common;

import com.example.dto.vacancy_dto.ResumeDto;
import com.example.dto.vacancy_dto.ResumeItemDto;
import com.example.model.AuthUser;
import com.example.model.Resume;
import com.example.model.User;
import com.example.repository.ResumeRepository;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final RequestTemplates requestTemplates;
    private final UserService userService;

    @Value("${hh.client-id}")
    private String clientId;

    public Resume getResumeById(String resumeId) {
        return resumeRepository.findByHhResumeId(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }


    public Resume getResumeByUser(User user) {
        return resumeRepository.findResumeByUser(user)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Rezyume.-Prosmotr-informacii/operation/get-mine-resumes">...</a>
     */
    public Resume getResumeFromHh(User user) {
        ResumeDto data = requestTemplates.getMineResume(user.getHhToken());
        Resume resume = new Resume();
        resume.setHhResumeId(data.items().get(0).id());
        resume.setUser(user);

        List<Resume> userResume = user.getResume();
        userResume.add(resume);
        return resumeRepository.save(resume);
    }

    public List<ResumeItemDto> getResumesFromHh(AuthUser authUser) {
        User user = authUser.getUser();
        ResumeDto data = requestTemplates.getMineResume(user.getHhToken());
        List<Resume> resumes = new ArrayList<>();
        for (int i = 0; i < data.items().size(); i++) {
            Resume resume = new Resume();
            resume.setHhResumeId(data.items().get(i).id());
            resume.setUser(user);
            resumes.add(resume);
            List<Resume> userResume = user.getResume();
            userResume.add(resume);
        }
        resumeRepository.saveAll(resumes);
        return data.items();
    }

}
