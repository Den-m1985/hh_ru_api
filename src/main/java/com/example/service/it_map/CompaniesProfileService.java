package com.example.service.it_map;

import com.example.dto.agregator_dto.CompaniesProfileRequest;
import com.example.dto.agregator_dto.CompaniesProfileResponse;
import com.example.dto.company.CompanyResponseDto;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.agregator.ExperienceGrade;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompaniesProfileService {
    CompanyService companyService;
    ExperienceGradeService experienceGradeService;
    CompetencyMatrixService competencyMatrixService;

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
                null,
                null,
                arrayOfCompanies
        );
    }
}
