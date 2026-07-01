package com.hireme.service;

import com.hireme.dto.CandidateResponse;
import com.hireme.dto.CandidateUpdateRequest;
import com.hireme.dto.SkillResponse;
import com.hireme.model.entity.Candidate;
import com.hireme.model.entity.Skill;
import com.hireme.model.entity.User;
import com.hireme.repository.ApplicationRepository;
import com.hireme.repository.CandidateRepository;
import com.hireme.repository.SkillRepository;
import com.hireme.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public CandidateResponse getProfile(Long id, Authentication authentication) {
        Candidate candidate = currentUserService.requireOwnedCandidate(id, authentication);
        return toResponse(candidate);
    }

    public CandidateResponse updateProfile(Long id, CandidateUpdateRequest request, Authentication authentication) {
        Candidate candidate = currentUserService.requireOwnedCandidate(id, authentication);
        validateProfileFields(request);
        validateEmailUnique(request.getEmail(), candidate.getId());

        candidate.setFirstName(request.getFirstName().trim());
        candidate.setLastName(request.getLastName().trim());
        candidate.setEmail(request.getEmail().trim());
        return toResponse(candidateRepository.save(candidate));
    }

    public CandidateResponse addSkill(Long candidateId, Long skillId, Authentication authentication) {
        Candidate candidate = currentUserService.requireOwnedCandidate(candidateId, authentication);
        Skill skill = skillRepository
                .findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono umiejętności"));

        boolean alreadyHas = candidate.getSkills().stream().anyMatch(s -> s.getId().equals(skillId));
        if (alreadyHas) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Posiadasz już tę umiejętność w swoim profilu");
        }

        candidate.getSkills().add(skill);
        return toResponse(candidateRepository.save(candidate));
    }

    public CandidateResponse removeSkill(Long candidateId, Long skillId, Authentication authentication) {
        Candidate candidate = currentUserService.requireOwnedCandidate(candidateId, authentication);
        boolean removed = candidate.getSkills().removeIf(s -> s.getId().equals(skillId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie posiadasz tej umiejętności w swoim profilu");
        }
        return toResponse(candidateRepository.save(candidate));
    }

    @Transactional
    public void deleteProfile(Long id, Authentication authentication) {
        Candidate candidate = currentUserService.requireOwnedCandidate(id, authentication);
        User user = candidate.getUser();

        applicationRepository.deleteAll(applicationRepository.findByCandidateId(id));
        candidateRepository.delete(candidate);
        userRepository.delete(user);
    }

    private void validateProfileFields(CandidateUpdateRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imię jest wymagane");
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nazwisko jest wymagane");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adres e-mail jest wymagany");
        }
    }

    private void validateEmailUnique(String email, Long candidateId) {
        candidateRepository.findByEmail(email.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(candidateId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ten adres e-mail jest już zajęty");
            }
        });
    }

    private CandidateResponse toResponse(Candidate candidate) {
        List<SkillResponse> skills = candidate.getSkills().stream()
                .map(s -> new SkillResponse(s.getId(), s.getName()))
                .toList();

        CandidateResponse response = new CandidateResponse();
        response.setId(candidate.getId());
        response.setFirstName(candidate.getFirstName());
        response.setLastName(candidate.getLastName());
        response.setEmail(candidate.getEmail());
        response.setSkills(skills);
        return response;
    }
}
