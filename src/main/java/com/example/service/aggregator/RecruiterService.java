package com.example.service.aggregator;

import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import com.example.mapper.RecruiterMapper;
import com.example.model.Company;
import com.example.model.Recruiter;
import com.example.repository.RecruiterRepository;
import com.example.service.company.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;
    private final RecruiterMapper recruiterMapper;
    private final CompanyService companyService;

    public Recruiter getRecruiterById(Integer recruiterId) {
        return recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new EntityNotFoundException("Recruiter with id: " + recruiterId + " not found"));
    }

    public RecruiterDto getRecruiterInfo(Integer recruiterId) {
        Recruiter recruiter = getRecruiterById(recruiterId);
        return recruiterMapper.toDto(recruiter);
    }

    public List<RecruiterDto> getRecruitersByCompany(Integer companyId) {
        List<Recruiter> recruiters = recruiterRepository.getRecruitersByCompanyId(companyId);
        return recruiterMapper.toDto(recruiters);
    }

    public List<RecruiterDto> findAll() {
        List<Recruiter> array = recruiterRepository.findAll();
        return recruiterMapper.toDto(array);
    }

    @Transactional
    public RecruiterDto saveRecruiter(RecruiterRequest request) {
       Optional <Company> companyOptional = companyService.getOptionalCompanyById(request.company());
        Company company = null;
       if (companyOptional.isPresent()){
           company = companyOptional.get();
       }
        Recruiter recruiter = recruiterMapper.toEntity(request, company);
        return recruiterMapper.toDto(recruiter);
    }

}
