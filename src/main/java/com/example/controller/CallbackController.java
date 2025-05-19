package com.example.controller;

import com.example.controller.interfaces.CallBackApi;
import com.example.service.CallBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CallbackController implements CallBackApi {
    private final CallBackService callBackService;

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code) {
        callBackService.processApp(code);
        return ResponseEntity.ok("Process successfully");
    }
}
