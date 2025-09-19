package com.example.service.aggregator;

import com.example.dto.agregator_dto.CompaniesProfileRequest;
import com.example.dto.agregator_dto.CompaniesProfileResponse;
import com.example.dto.company.CompanyResponseDto;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.agregator.ExperienceGrade;
import com.example.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompaniesProfileService {
    private final CompanyService companyService;
    private final ExperienceGradeService experienceGradeService;
    private final CompetencyMatrixService competencyMatrixService;

    public CompaniesProfileResponse getCompaniesProfile(CompaniesProfileRequest request) {
        ExperienceGrade experience = experienceGradeService.getExperienceGradeById(request.experienceGrade());
        CompetencyMatrix competency = competencyMatrixService.getCompetencyMatrixBySpec(request.specialization());

        List<CompanyResponseDto> arrayOfCompanies = companyService.getCompaniesByCategories(request.categories());
        return companiesProfileMapper(experience, competency, arrayOfCompanies);
    }

    private CompaniesProfileResponse companiesProfileMapper(ExperienceGrade experience, CompetencyMatrix competency, List<CompanyResponseDto> arrayOfCompanies) {
        return new CompaniesProfileResponse(
                experience.getName(),
                experience.getDescription(),
                competency.getSpecialization(),
                competency.getCompetencies(),
                competency.getTechnicalQuestions(),
                arrayOfCompanies
        );
    }
}
