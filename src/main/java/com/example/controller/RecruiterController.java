package com.example.controller;

import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import com.example.service.aggregator.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/recruiter")
public class RecruiterController {
    private final RecruiterService recruiterService;

    @GetMapping("/{id}")
    public ResponseEntity<RecruiterDto> getRecruiterById(@PathVariable Integer id) {
        return ResponseEntity.ok(recruiterService.getRecruiterInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecruiterDto>> getAllRecruiters() {
        return ResponseEntity.ok(recruiterService.findAll());
    }

    @GetMapping("/all_by_company/{companyId}")
    public ResponseEntity<List<RecruiterDto>> getAllRecruitersByCompany(@PathVariable Integer companyId) {
        return ResponseEntity.ok(recruiterService.getRecruitersByCompany(companyId));
    }

    @PostMapping("/add")
    public ResponseEntity<RecruiterDto> addRecruiter(@RequestBody RecruiterRequest request) {
        return ResponseEntity.ok(recruiterService.saveRecruiter(request));
    }
}
