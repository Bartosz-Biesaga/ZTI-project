package com.hireme.service;

import com.hireme.model.entity.Candidate;
import com.hireme.model.entity.Company;
import com.hireme.model.entity.User;
import com.hireme.repository.CandidateRepository;
import com.hireme.repository.CompanyRepository;
import com.hireme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    public Candidate requireOwnedCandidate(Long candidateId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Candidate candidate = candidateRepository
                .findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono kandydata"));
        if (!candidate.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu do tego profilu");
        }
        return candidate;
    }

    public Company requireOwnedCompany(Long companyId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Company company = companyRepository
                .findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono firmy"));
        if (!company.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu do tego profilu");
        }
        return company;
    }
}
