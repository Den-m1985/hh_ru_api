package com.example.controller.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface CallBackApi {

    ResponseEntity<String> callback(@RequestParam String code);
}
