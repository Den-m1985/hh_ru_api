package com.example.controller;

import com.example.controller.interfaces.CompetencyMatrixApi;
import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import com.example.service.aggregator.CompetencyMatrixService;
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
@RequestMapping("/v1/competency")
public class CompetencyMatrixController implements CompetencyMatrixApi {
    private final CompetencyMatrixService competencyMatrixService;

    @GetMapping("/{id}")
    public ResponseEntity<CompetencyMatrixResponse> getCompetencyMatrixById(@PathVariable Integer id) {
        return ResponseEntity.ok(competencyMatrixService.getCompetencyMatrixDto(id));
    }

    @GetMapping("/by_spec/{specialization}")
    public ResponseEntity<CompetencyMatrixResponse> getCompetencyBySpecialisation(@PathVariable String specialization) {
        return ResponseEntity.ok(competencyMatrixService.getCompetencyMatrixDto(specialization));
    }

    @PostMapping("/add")
    public ResponseEntity<CompetencyMatrixResponse> addCompetencyMatrix(@RequestBody CompetencyMatrixRequest request) {
        return ResponseEntity.ok(competencyMatrixService.addCompetencyMatrix(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompetencyMatrixResponse>> getAllCompetencyMatrix() {
        return ResponseEntity.ok(competencyMatrixService.getAllCompetencyMatrixDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompetencyMatrix(@PathVariable Integer id) {
        competencyMatrixService.deleteCompetencyMatrix(id);
        return ResponseEntity.noContent().build();
    }
}
