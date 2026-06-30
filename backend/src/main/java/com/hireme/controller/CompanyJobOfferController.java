package com.hireme.controller;

import com.hireme.dto.JobOfferRequest;
import com.hireme.dto.JobOfferResponse;
import com.hireme.service.JobOfferService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/offers")
public class CompanyJobOfferController {

    @Autowired
    private JobOfferService jobOfferService;

    @GetMapping
    public List<JobOfferResponse> listOffers(@PathVariable Long companyId, Authentication authentication) {
        return jobOfferService.listByCompany(companyId, authentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobOfferResponse createOffer(
            @PathVariable Long companyId, @RequestBody JobOfferRequest request, Authentication authentication) {
        return jobOfferService.create(companyId, request, authentication);
    }

    @GetMapping("/{offerId}")
    public JobOfferResponse getOffer(
            @PathVariable Long companyId, @PathVariable Long offerId, Authentication authentication) {
        return jobOfferService.getForCompany(companyId, offerId, authentication);
    }

    @PutMapping("/{offerId}")
    public JobOfferResponse updateOffer(
            @PathVariable Long companyId,
            @PathVariable Long offerId,
            @RequestBody JobOfferRequest request,
            Authentication authentication) {
        return jobOfferService.update(companyId, offerId, request, authentication);
    }

    @DeleteMapping("/{offerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(
            @PathVariable Long companyId, @PathVariable Long offerId, Authentication authentication) {
        jobOfferService.delete(companyId, offerId, authentication);
    }
}
