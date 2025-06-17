package com.example.service.common;

import com.example.dto.vacancy_dto.ResumeDto;
import com.example.dto.vacancy_dto.ResumeItemDto;
import com.example.model.AuthUser;
import com.example.model.Resume;
import com.example.model.User;
import com.example.repository.ResumeRepository;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final RequestTemplates requestTemplates;
    private final UserService userService;

    public Resume getResumeById(String resumeId) {
        return resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Rezyume.-Prosmotr-informacii/operation/get-mine-resumes">...</a>
     */
    public Resume getResumeFromHh(User user) {
        ResumeDto data = requestTemplates.getMineResume(user.getHhToken());
        Resume resume = new Resume();
        resume.setResumeId(data.items().get(0).id());
        resume.setUser(user);

        List<Resume> userResume = user.getResume();
        userResume.add(resume);
        return resumeRepository.save(resume);
    }

    @Transactional
    public List<ResumeItemDto> getResumeItemDto(AuthUser authUser) {
        List<Resume> existingResumes = getResumesFromHh(authUser);
        List<ResumeItemDto> result = new ArrayList<>();
        for (Resume resume : existingResumes) {
            ResumeItemDto resumeItemDto = new ResumeItemDto(resume.getResumeTitle(), resume.getResumeId());
            result.add(resumeItemDto);
        }
        return result;
    }

    public List<Resume> getResumesFromHh(AuthUser authUser) {
        User user = userService.getUserById(authUser.getUser().getId());
        ResumeDto data = requestTemplates.getMineResume(user.getHhToken());
        List<ResumeItemDto> items = data.items();
        if (items == null) {
            return Collections.emptyList();
        }
        List<Resume> existingResumes = user.getResume();
        if (existingResumes != null) {
            resumeRepository.deleteAll(existingResumes);
            existingResumes.clear();
        }else {
            existingResumes = new ArrayList<>();
        }

        for (ResumeItemDto item : items) {
            if (!isContainsInList(existingResumes, item.id())) {
                Resume resume = new Resume();
                resume.setResumeId(item.id());
                resume.setResumeTitle(item.title());
                resume.setUser(user);
                existingResumes.add(resume);
            }
        }
        resumeRepository.saveAll(existingResumes);
        return existingResumes;
    }

    private boolean isContainsInList(List<Resume> resumes, String resumeId) {
        for (Resume resume : resumes) {
            if (resume.getResumeId().equals(resumeId)) {
                return true;
            }
        }
        return false;
    }

}
