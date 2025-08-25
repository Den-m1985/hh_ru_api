package com.example.service.strategy;

import com.example.dto.interfaces.BaseResumeDto;
import com.example.dto.vacancy_dto.ResumeDto;
import com.example.dto.vacancy_dto.ResumeItemDto;
import com.example.enums.Device;
import com.example.model.AuthUser;
import com.example.model.Resume;
import com.example.model.User;
import com.example.repository.ResumeRepository;
import com.example.service.HhTokenService;
import com.example.service.common.OAuthStateGenerator;
import com.example.service.common.UserService;
import com.example.util.HeadHunterProperties;
import com.example.util.QueryBuilder;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("headhunterStrategy")
@RequiredArgsConstructor
public class HeadHunterStrategy implements IOAuthStrategy {
    private final OAuthStateGenerator oAuthStateGenerator;
    private final HeadHunterProperties headHunterProperties;
    private final ResumeRepository resumeRepository;
    private final RequestTemplates requestTemplates;
    private final UserService userService;
    private final HhTokenService hhTokenService;

    @Override
    public String getAuthUrl(AuthUser authUser) {
        String state = oAuthStateGenerator.generateEncryptedState(authUser.getUser().getId(), Device.ANDROID);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", headHunterProperties.getClientId());
        params.put("state", state);
        String url = headHunterProperties.getBaseUrl() + "/oauth/authorize" + "?" + QueryBuilder.buildQuery(params);
        System.out.println(url);
        return url;
    }

    @Override
    @Transactional
    public List<BaseResumeDto> getMineResume(AuthUser authUser) {
        List<Resume> existingResumes = getResumesFromHh(authUser);
        List<BaseResumeDto> result = new ArrayList<>();
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
        } else {
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

    @Override
    public boolean isTokenGood(AuthUser authUser) {
        return hhTokenService.isTokenGood(authUser.getUser().getHhToken());
    }
}
