package com.example.controller.it_map;

import com.example.controller.interfaces.CompetencyMatrixApi;
import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import com.example.dto.it_map.CompetencyAreasResponse;
import com.example.dto.it_map.CompetencyMatrixFilterRequest;
import com.example.service.it_map.CompetencyMatrixService;
import jakarta.validation.Valid;
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
@RequestMapping("/v1/it_map/competency")
public class CompetencyMatrixController implements CompetencyMatrixApi {
    private final CompetencyMatrixService competencyMatrixService;

    @GetMapping("/{id}")
    public ResponseEntity<CompetencyMatrixResponse> getCompetencyMatrixById(@PathVariable Integer id) {
        return ResponseEntity.ok(competencyMatrixService.getCompetencyMatrixDto(id));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<CompetencyAreasResponse>> getFilteredCompetencyMatrix(
            @RequestBody @Valid CompetencyMatrixFilterRequest request) {
        return ResponseEntity.ok(competencyMatrixService.getCompetencyMatrixFilteredDto(request));
    }

    @PostMapping("/add")
    public ResponseEntity<CompetencyMatrixResponse> addCompetencyMatrix(@RequestBody CompetencyMatrixRequest request) {
        return ResponseEntity.ok(competencyMatrixService.returnCompetencyMatrixDto(request));
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
