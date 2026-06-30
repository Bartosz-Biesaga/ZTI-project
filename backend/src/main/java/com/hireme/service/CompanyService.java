package com.hireme.service;

import com.hireme.dto.CompanyResponse;
import com.hireme.dto.CompanyUpdateRequest;
import com.hireme.model.entity.Company;
import com.hireme.model.entity.JobOffer;
import com.hireme.model.entity.User;
import com.hireme.repository.ApplicationRepository;
import com.hireme.repository.CompanyRepository;
import com.hireme.repository.JobOfferRepository;
import com.hireme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public CompanyResponse getProfile(Long id, Authentication authentication) {
        Company company = currentUserService.requireOwnedCompany(id, authentication);
        return toResponse(company);
    }

    public CompanyResponse updateProfile(Long id, CompanyUpdateRequest request, Authentication authentication) {
        Company company = currentUserService.requireOwnedCompany(id, authentication);
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nazwa firmy jest wymagana");
        }
        company.setName(request.getName().trim());
        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public void deleteProfile(Long id, Authentication authentication) {
        Company company = currentUserService.requireOwnedCompany(id, authentication);
        User user = company.getUser();

        for (JobOffer offer : jobOfferRepository.findByCompany_Id(id)) {
            applicationRepository.deleteAll(applicationRepository.findByJobOfferId(offer.getId()));
            jobOfferRepository.delete(offer);
        }

        companyRepository.delete(company);
        userRepository.delete(user);
    }

    private CompanyResponse toResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        return response;
    }
}
