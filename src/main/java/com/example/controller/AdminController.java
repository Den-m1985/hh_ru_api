package com.example.controller;

import com.example.dto.user.UserInfoDto;
import com.example.model.AuthUser;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
public class AdminController {
    private final AdminUserService adminUserService;

    // TODO should be replased by "spring-boot-starter-actuator"
    @GetMapping("/test")
    public ResponseEntity<Boolean> getTest() {
        return ResponseEntity.ok(true);
    }

    @Hidden
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @Hidden
    @GetMapping("/by-id/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> getUserDetails(
            @PathVariable Integer userId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        User user = authUser.getUser();
        if (user.getRole().equals(RoleEnum.SUPER_ADMIN)) {
            return ResponseEntity.ok(adminUserService.getUserInfoBySuperAdmin(userId));
        } else if (user.getRole().equals(RoleEnum.ADMIN)) {
            return ResponseEntity.ok(adminUserService.getUserInfoByAdmin(userId));
        }
        return ResponseEntity.status(403).build();
    }

    @Hidden
    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> getUserDetails(
            @PathVariable String email,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        User user = authUser.getUser();
        if (user.getRole().equals(RoleEnum.SUPER_ADMIN)) {
            return ResponseEntity.ok(adminUserService.getUserInfoBySuperAdmin(email));
        } else if (user.getRole().equals(RoleEnum.ADMIN)) {
            return ResponseEntity.ok(adminUserService.getUserInfoByAdmin(email));
        }
        return ResponseEntity.status(403).build();
    }
}
