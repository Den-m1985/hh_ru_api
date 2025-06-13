package com.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
public class TestController {

    // TODO should be replased by "spring-boot-starter-actuator"
    @GetMapping("/test")
    public ResponseEntity<Boolean> getTest() {
        return ResponseEntity.ok(true);
    }
}
