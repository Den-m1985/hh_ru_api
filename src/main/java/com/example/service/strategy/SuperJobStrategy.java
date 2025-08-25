package com.example.service.strategy;

import com.example.dto.interfaces.BaseResumeDto;
import com.example.dto.superjob.SuperjobResumeDto;
import com.example.dto.superjob.resume.ResumeObject;
import com.example.dto.superjob.resume.SuperJobResumeResponse;
import com.example.enums.Device;
import com.example.model.AuthUser;
import com.example.model.SuperjobResume;
import com.example.model.User;
import com.example.service.common.OAuthStateGenerator;
import com.example.service.common.UserService;
import com.example.service.superjob.SuperjobResumeService;
import com.example.service.superjob.SuperjobTokenService;
import com.example.util.QueryBuilder;
import com.example.util.RequestTemplates;
import com.example.util.SuperjobProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("superjobStrategy")
@RequiredArgsConstructor
public class SuperJobStrategy implements IOAuthStrategy {
    private final OAuthStateGenerator oAuthStateGenerator;
    private final SuperjobProperties superjobProperties;
    private final RequestTemplates requestTemplates;
    private final UserService userService;
    private final SuperjobResumeService superjobResumeService;
    private final SuperjobTokenService superjobTokenService;

    @Override
    public String getAuthUrl(AuthUser authUser) {
        String state = oAuthStateGenerator.generateEncryptedState(authUser.getUser().getId(), Device.ANDROID);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", superjobProperties.clientId());
        params.put("redirect_uri", superjobProperties.redirectUri());
        params.put("state", state);
        String url = superjobProperties.baseUrl() + "/authorize" + "?" + QueryBuilder.buildQuery(params);
        System.out.println(url);
        return url;
    }

    @Override
    @Transactional
    public List<BaseResumeDto> getMineResume(AuthUser authUser) {
        List<SuperjobResume> existingResumes = getResumeFromSuperjob(authUser.getUser());
        List<BaseResumeDto> result = new ArrayList<>();
        for (SuperjobResume resume : existingResumes) {
            SuperjobResumeDto resumeItemDto = new SuperjobResumeDto(resume.getResumeTitle(), resume.getResumeId());
            result.add(resumeItemDto);
        }
        return result;
    }

    public List<SuperjobResume> getResumeFromSuperjob(User user) {
        String url = superjobProperties.baseUrlApi() + "/2.0/user_cvs/";
        SuperJobResumeResponse data = requestTemplates.getResumes(url, user.getSuperjobToken());
        user = userService.getUserById(user.getId());
        List<SuperjobResume> existingResumes = user.getSuperjobResumes();
        if (existingResumes != null) {
            superjobResumeService.deleteAll(existingResumes);
            existingResumes.clear();
        } else {
            existingResumes = new ArrayList<>();
        }
        for (ResumeObject resume : data.objects()) {
            SuperjobResume newResume = new SuperjobResume();
            newResume.setResumeTitle(resume.name());
            newResume.setResumeId(resume.id());
            newResume.setUser(user);
            existingResumes.add(newResume);
        }
        return superjobResumeService.saveAll(existingResumes);
    }

    @Override
    public boolean isTokenGood(AuthUser authUser) {
        return superjobTokenService.isTokenGood(authUser.getUser().getSuperjobToken());
    }
}
