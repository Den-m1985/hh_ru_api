package com.example.controller;

import com.example.controller.interfaces.RecruiterApi;
import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import com.example.service.aggregator.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/recruiters")
public class RecruiterController implements RecruiterApi {
    private final RecruiterService recruiterService;

    @GetMapping("/{id}")
    public ResponseEntity<RecruiterDto> getRecruiterById(@PathVariable Integer id) {
        return ResponseEntity.ok(recruiterService.getRecruiterInfo(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<RecruiterDto> getRecruiterByEmail(@PathVariable String email) {
        return ResponseEntity.ok(recruiterService.getRecruiterByEmail(email));
    }

    @GetMapping("/telegram/{telegram}")
    public ResponseEntity<RecruiterDto> getRecruiterByTelegram(@PathVariable String telegram) {
        return ResponseEntity.ok(recruiterService.getRecruiterByTelegram(telegram));
    }

    @GetMapping("/linkedIn/{linkedIn}")
    public ResponseEntity<RecruiterDto> getRecruiterByLinkedIn(@PathVariable String linkedIn) {
        return ResponseEntity.ok(recruiterService.getRecruiterByLinkedIn(linkedIn));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable Integer id) {
        recruiterService.deleteRecruiter(id);
        return ResponseEntity.noContent().build();
    }
}
