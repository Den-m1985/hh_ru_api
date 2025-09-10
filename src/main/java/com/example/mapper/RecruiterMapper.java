package com.example.mapper;

import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import com.example.model.Company;
import com.example.model.Recruiter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecruiterMapper {

    public Recruiter toEntity(RecruiterRequest recruiterDto, Company company) {
        Recruiter recruiter = new Recruiter();
        recruiter.setCompany(company);
        recruiter.setFirstName(recruiterDto.firstName());
        recruiter.setLastName(recruiterDto.lastName());
        recruiter.setContactTelegram(recruiterDto.contactTelegram());
        recruiter.setContactLinkedIn(recruiterDto.contactLinkedIn());
        recruiter.setEmail(recruiterDto.email());
        return recruiter;
    }

    public RecruiterDto toDto(Recruiter recruiter) {
        return new RecruiterDto(
                recruiter.getId(),
                recruiter.getCreatedAt(),
                recruiter.getUpdatedAt(),
                recruiter.getFirstName(),
                recruiter.getLastName(),
                recruiter.getContactTelegram(),
                recruiter.getContactLinkedIn(),
                recruiter.getEmail(),
                recruiter.getCompany().getName()
        );
    }

    public List<RecruiterDto> toDto(List<Recruiter> entities) {
        return entities.stream().map(this::toDto).toList();
    }

}
