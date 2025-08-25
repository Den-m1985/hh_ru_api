package com.example.service.strategy;

import com.example.dto.interfaces.BaseResumeDto;
import com.example.model.AuthUser;

import java.util.List;

public interface IOAuthStrategy {

    String getAuthUrl(AuthUser authUser);
    List<BaseResumeDto> getMineResume(AuthUser authUser);
    boolean isTokenGood(AuthUser authUser);

}
