package com.example.service.admin;

import com.example.dto.user.UserInfoDto;
import com.example.dto.user.UserInfoDtoAdmin;
import com.example.dto.user.UserInfoDtoFull;
import com.example.model.User;
import com.example.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<UserInfoDto> getAllUsers() {
        return userService.findAll().stream()
                .map(UserInfoDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public UserInfoDtoAdmin getUserInfoByAdmin(Integer userId) {
        User user = userService.getUserById(userId);
        return new UserInfoDtoAdmin(user);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public UserInfoDtoAdmin getUserInfoByAdmin(String email) {
        User user = userService.getUserByEmail(email);
        return new UserInfoDtoAdmin(user);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserInfoDtoFull getUserInfoBySuperAdmin(Integer userId) {
        User user = userService.getUserById(userId);
        return new UserInfoDtoFull(user);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserInfoDtoFull getUserInfoBySuperAdmin(String email) {
        User user = userService.getUserByEmail(email);
        return new UserInfoDtoFull(user);
    }

}
