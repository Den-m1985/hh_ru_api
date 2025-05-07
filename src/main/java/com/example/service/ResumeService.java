package com.example.service;

import com.example.dto.vacancy_dto.ResumeDto;
import com.example.model.Resume;
import com.example.repository.ResumeRepository;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository repository;
    private final RequestTemplates requestTemplates;

    @Value("${hh.client-id}")
    private String clientId;

    public Resume getResume() {
        return repository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    // TODO save several resume or choose one
    public void getResumeFromHh() {
        ResumeDto data = requestTemplates.getMineResume();
        System.out.println(data);
        Resume resume = new Resume();
        resume.setResumeId(data.items().get(0).id());
        resume.setClientId(clientId);
        repository.save(resume);
    }
}
