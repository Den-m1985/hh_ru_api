package com.example.controller;

import com.example.controller.interfaces.CompanyCategoryApi;
import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.service.aggregator.CompanyCategoryService;
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
@RequestMapping("/v1/category")
public class CompanyCategoryController implements CompanyCategoryApi {
    private final CompanyCategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CompanyCategoryDto> getCompanyCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCompanyCategoryDto(id));
    }

    @GetMapping("/get_by_name/{name}")
    public ResponseEntity<CompanyCategoryDto> getCompanyCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCompanyCategoryDto(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyCategoryDto>> getAllCompanyCategories() {
        return ResponseEntity.ok(categoryService.getAllCompaniesCategory());
    }

    @PostMapping("/add")
    public ResponseEntity<CompanyCategoryDto> addCompanyCategory(@RequestBody CompanyCategoryDto response) {
        return ResponseEntity.ok(categoryService.createCategory(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompanyCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
