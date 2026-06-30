package com.hireme.controller;

import com.hireme.dto.ApplicationResponse;
import com.hireme.dto.ApplicationUpdateRequest;
import com.hireme.service.ApplicationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies/{companyId}/offers/{offerId}/applications")
public class CompanyApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public List<ApplicationResponse> listApplications(
            @PathVariable Long companyId,
            @PathVariable Long offerId,
            Authentication authentication) {
        return applicationService.listForOffer(companyId, offerId, authentication);
    }

    @PatchMapping("/{applicationId}")
    public ApplicationResponse updateApplication(
            @PathVariable Long companyId,
            @PathVariable Long offerId,
            @PathVariable Long applicationId,
            @RequestBody ApplicationUpdateRequest request,
            Authentication authentication) {
        return applicationService.update(companyId, offerId, applicationId, request, authentication);
    }
}
