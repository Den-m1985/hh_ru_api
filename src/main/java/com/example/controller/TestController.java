package com.example.controller;

import com.example.model.User;
import com.example.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
public class TestController {
    private final UserService userService;

    // TODO should be replased by "spring-boot-starter-actuator"
    @GetMapping("/test")
    public ResponseEntity<Boolean> getTest() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
}
