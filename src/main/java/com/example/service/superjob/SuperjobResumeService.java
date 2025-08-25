package com.example.service.superjob;

import com.example.model.SuperjobResume;
import com.example.model.User;
import com.example.repository.SuperjobResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperjobResumeService {
    private final SuperjobResumeRepository superjobResumeRepository;

    public void save(SuperjobResume superjobResume) {
        superjobResumeRepository.save(superjobResume);
    }

    public List<SuperjobResume> saveAll(List<SuperjobResume> superjobResumes) {
        return superjobResumeRepository.saveAll(superjobResumes);
    }

    public void deleteAll(List<SuperjobResume> resumes) {
        superjobResumeRepository.deleteAll(resumes);
    }

    public SuperjobResume getResumeByUser(User user) {
        return superjobResumeRepository.findSuperjobResumeByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Superjob_resume with user_id: " + user.getId() + " not found"));
    }
}
