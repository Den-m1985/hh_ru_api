package com.example.service.it_map;

import com.example.dto.company.CompanyResponseDto;
import com.example.exceptions.CompanyCreationException;
import com.example.exceptions.FileStorageException;
import com.example.mapper.CompanyMapper;
import com.example.model.Recruiter;
import com.example.model.it_map.Company;
import com.example.model.it_map.CompanyCategory;
import com.example.repository.RecruiterRepository;
import com.example.repository.it_map.CompanyRepository;
import com.example.service.common.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;
    CompanyCategoryService categoryService;
    RecruiterRepository recruiterRepository;
    CompanyMapper companyMapper;
    FileStorageService fileStorageService;


    public Company getCompanyById(Integer id) {
        return companyRepository.findCompanyById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company with id: " + id + " not found"));
    }

    public Optional<Company> getOptionalCompanyById(Integer id) {
        return companyRepository.findCompanyById(id);
    }

    @Transactional
    public CompanyResponseDto addCompany(CompanyResponseDto dto) {
        Company company = createCompany(dto);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    @Transactional
    public CompanyResponseDto addCompany(CompanyResponseDto dto, MultipartFile file) {
        String fileName = saveFile(file);
        dto.setLogoUrl(fileName);
        Company company = createCompany(dto);
        try {
            company = companyRepository.save(company);
        } catch (Exception dbException) {
            // ЛОГИКА КОМПЕНСАЦИИ: Транзакция БД провалилась
            // Удаляем новый файл, который был успешно записан на диск,
            // но не был зафиксирован в БД.
            fileStorageService.deleteFile(fileName);
            // Повторно выбрасываем исключение, чтобы Spring откатил транзакцию
            throw new CompanyCreationException("DB save failed, new file deleted.", dbException);
        }
        return companyMapper.toDto(company);
    }

    private Company createCompany(CompanyResponseDto dto) {
        Set<CompanyCategory> category = categoryService.getAllCategoryByArray(dto.getCategory());
        List<Recruiter> recruiters = new ArrayList<>();
        if (dto.getRecruiters() != null && !dto.getRecruiters().isEmpty()) {
            recruiters = recruiterRepository.findAllById(dto.getRecruiters());
        }
        Company company = companyMapper.toEntity(dto, category, recruiters);
        for (Recruiter recruiter : recruiters) {
            recruiter.setCompany(company);
        }
        return company;
    }

    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companyMapper.toDto(companies);
    }

    public CompanyResponseDto getCompanyDto(Integer id) {
        Company company = getCompanyById(id);
        return companyMapper.toDto(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getCompaniesByCategories(List<Integer> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        List<Company> companies = companyRepository.findByCategoryIds(categories);
        return companies.stream()
                .map(companyMapper::toDto)
                .toList();
    }

    public void deleteCompany(Integer id) {
        Company company = getCompanyById(id);
        if (company.getLogoPath() != null) {
            fileStorageService.deleteFile(company.getLogoPath());
        }
        companyRepository.delete(company);
    }

    @Transactional
    public CompanyResponseDto updateCompanyLogo(Integer companyId, MultipartFile file) {
        Company company = getCompanyById(companyId);
        String oldLogoPath = company.getLogoPath();
        String newFileName = saveFile(file);
        try {
            company.setLogoPath(newFileName);
            companyRepository.save(company);
            if (oldLogoPath != null) {
                fileStorageService.deleteFile(oldLogoPath);
            }
        } catch (Exception dbException) {
            // ЛОГИКА КОМПЕНСАЦИИ: Транзакция БД провалилась
            // Удаляем новый файл, который был успешно записан на диск,
            // но не был зафиксирован в БД.
            fileStorageService.deleteFile(newFileName);
            // Повторно выбрасываем исключение, чтобы Spring откатил транзакцию
            throw new CompanyCreationException("DB save failed, new file deleted.", dbException);
        }
        return companyMapper.toDto(company);
    }

    private String saveFile(MultipartFile file) {
        String fileName = fileStorageService.generateUniqueFileName(file);
        try {
            fileStorageService.uploadFile(file, fileName);
            return fileName;
        } catch (IOException e) {
            throw new FileStorageException("Ошибка сохранения файла", e);
        }
    }

}
