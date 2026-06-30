package com.hireme.controller;

import com.hireme.dto.ApplicationCreateRequest;
import com.hireme.dto.ApplicationResponse;
import com.hireme.service.ApplicationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidates/{candidateId}/applications")
public class CandidateApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(
            @PathVariable Long candidateId,
            @RequestBody ApplicationCreateRequest request,
            Authentication authentication) {
        return applicationService.apply(candidateId, request, authentication);
    }

    @GetMapping
    public List<ApplicationResponse> listApplications(
            @PathVariable Long candidateId, Authentication authentication) {
        return applicationService.listForCandidate(candidateId, authentication);
    }
}
