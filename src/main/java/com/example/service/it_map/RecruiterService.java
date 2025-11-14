package com.example.service.it_map;

import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import com.example.mapper.RecruiterMapper;
import com.example.model.it_map.Company;
import com.example.model.Recruiter;
import com.example.repository.RecruiterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecruiterService {
    RecruiterRepository recruiterRepository;
    RecruiterMapper recruiterMapper;
    CompanyService companyService;

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

    public RecruiterDto getRecruiterByEmail(String email) {
        Recruiter recruiter = recruiterRepository.getRecruiterByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Recruiter with email: " + email + " not found"));
        return recruiterMapper.toDto(recruiter);
    }

    public RecruiterDto getRecruiterByTelegram(String telegram) {
        Recruiter recruiter = recruiterRepository.getRecruitersByTelegram(telegram)
                .orElseThrow(() -> new EntityNotFoundException("Recruiter with telegram: " + telegram + " not found"));
        return recruiterMapper.toDto(recruiter);
    }

    public RecruiterDto getRecruiterByLinkedIn(String linkedIn) {
        Recruiter recruiter = recruiterRepository.getRecruitersByLinkedIn(linkedIn)
                .orElseThrow(() -> new EntityNotFoundException("Recruiter with linkedIn: " + linkedIn + " not found"));
        return recruiterMapper.toDto(recruiter);
    }

    public List<RecruiterDto> findAll() {
        List<Recruiter> array = recruiterRepository.findAll();
        return recruiterMapper.toDto(array);
    }

    @Transactional
    public RecruiterDto saveRecruiter(RecruiterRequest request) {
        Optional<Company> companyOptional = companyService.getOptionalCompanyById(request.company());
        Company company = null;
        if (companyOptional.isPresent()) {
            company = companyOptional.get();
        }
        Recruiter recruiter = recruiterMapper.toEntity(request, company);
        recruiter = recruiterRepository.save(recruiter);
        return recruiterMapper.toDto(recruiter);
    }

    public void deleteRecruiter(Integer id) {
        Recruiter recruiter = getRecruiterById(id);
        recruiterRepository.delete(recruiter);
    }
}
