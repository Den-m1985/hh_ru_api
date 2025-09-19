package com.example.controller;

import com.example.controller.interfaces.ExperienceGradeApi;
import com.example.dto.agregator_dto.ExperienceGradeRequest;
import com.example.dto.agregator_dto.ExperienceGradeResponse;
import com.example.service.aggregator.ExperienceGradeService;
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
@RequestMapping("/v1/grade")
public class ExperienceGradeController implements ExperienceGradeApi {
    private final ExperienceGradeService experienceGradeService;

    @GetMapping("/{id}")
    public ResponseEntity<ExperienceGradeResponse> getGradeById(@PathVariable Integer id) {
        return ResponseEntity.ok(experienceGradeService.getGradeByIdDto(id));
    }

    @GetMapping("/by_name/{name}")
    public ResponseEntity<ExperienceGradeResponse> getGradeByName(@PathVariable String name) {
        return ResponseEntity.ok(experienceGradeService.getGradeByNameDto(name));
    }

    @PostMapping("/add")
    public ResponseEntity<ExperienceGradeResponse> addGrade(@RequestBody ExperienceGradeRequest request) {
        return ResponseEntity.ok(experienceGradeService.addExperienceGrade(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExperienceGradeResponse>> getAllGrades() {
        return ResponseEntity.ok(experienceGradeService.getAllGrades());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Integer id) {
        experienceGradeService.deleteExperienceGrade(id);
        return ResponseEntity.noContent().build();
    }
}
