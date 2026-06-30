package com.hireme.service;

import com.hireme.dto.RegisterRequest;
import com.hireme.dto.UserResponse;
import com.hireme.model.entity.Candidate;
import com.hireme.model.entity.Company;
import com.hireme.model.entity.User;
import com.hireme.model.enums.Role;
import com.hireme.repository.CandidateRepository;
import com.hireme.repository.CompanyRepository;
import com.hireme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nie można zarejestrować konta administratora");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adres e-mail jest wymagany");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hasło jest wymagane");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ten adres e-mail jest już zarejestrowany");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user = userRepository.save(user);

        if (request.getRole() == Role.CANDIDATE) {
            Candidate candidate = new Candidate();
            candidate.setUser(user);
            candidate.setFirstName(request.getFirstName());
            candidate.setLastName(request.getLastName());
            candidate.setEmail(request.getEmail());
            candidateRepository.save(candidate);
        } else if (request.getRole() == Role.COMPANY) {
            Company company = new Company();
            company.setUser(user);
            company.setName(request.getCompanyName());
            companyRepository.save(company);
        }

        return toUserResponse(user);
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        Long profileId = null;
        if (user.getRole() == Role.CANDIDATE) {
            profileId = candidateRepository
                    .findByUser_Id(user.getId())
                    .map(Candidate::getId)
                    .orElse(null);
        } else if (user.getRole() == Role.COMPANY) {
            profileId = companyRepository
                    .findByUser_Id(user.getId())
                    .map(Company::getId)
                    .orElse(null);
        }
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), profileId);
    }
}
