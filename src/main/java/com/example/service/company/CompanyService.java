package com.example.service.company;

import com.example.dto.company.CompanyResponseDto;
import com.example.exceptions.FileStorageException;
import com.example.mapper.CompanyMapper;
import com.example.model.Company;
import com.example.model.CompanyCategory;
import com.example.model.Recruiter;
import com.example.repository.CompanyRepository;
import com.example.repository.RecruiterRepository;
import com.example.service.aggregator.CompanyCategoryService;
import com.example.service.common.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyCategoryService categoryService;
    private final RecruiterRepository recruiterRepository;
    private final CompanyMapper companyMapper;
    private final FileStorageService fileStorageService;

    public Company getCompanyByName(String name) {
        return companyRepository.findCompanyByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Company with name: " + name + " not found"));
    }

    public Company getCompanyById(Integer id) {
        return companyRepository.findCompanyById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company with id: " + id + " not found"));
    }

    public Optional<Company> getOptionalCompanyById(Integer id) {
        return companyRepository.findCompanyById(id);
    }

    @Transactional
    public CompanyResponseDto addCompany(CompanyResponseDto dto) {
        Company company = createCompany(dto, null);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    @Transactional
    public CompanyResponseDto addCompany(CompanyResponseDto dto, MultipartFile file) {
        String fileName = saveFile(file);
        Company company = createCompany(dto, fileName);
        try {
            company = companyRepository.save(company);
        } catch (Exception dbException) {
            // ЛОГИКА КОМПЕНСАЦИИ: Транзакция БД провалилась
            // Удаляем новый файл, который был успешно записан на диск,
            // но не был зафиксирован в БД.
            fileStorageService.deleteFile(fileName);
            // Повторно выбрасываем исключение, чтобы Spring откатил транзакцию
            throw new RuntimeException("DB save failed, new file deleted.", dbException);
        }
        return companyMapper.toDto(company);
    }

    private Company createCompany(CompanyResponseDto dto, String logoPath) {
        CompanyCategory category = categoryService.getOrCreateCategory(dto.category());
        List<Recruiter> recruiters = new ArrayList<>();
        if (dto.recruiters() != null && !dto.recruiters().isEmpty()) {
            recruiters = recruiterRepository.findAllById(dto.recruiters());
        }
        Company company = companyMapper.toEntity(dto, category, recruiters, logoPath);
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

    public List<CompanyResponseDto> getCompaniesByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        List<Company> companies = companyRepository.findByCategoryNames(categories);
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
            throw new RuntimeException("DB save failed, new file deleted.", dbException);
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
