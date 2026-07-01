package com.hireme.controller;

import com.hireme.dto.CandidateResponse;
import com.hireme.dto.CandidateUpdateRequest;
import com.hireme.service.CandidateService;
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
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/{id}")
    public CandidateResponse getProfile(@PathVariable Long id, Authentication authentication) {
        return candidateService.getProfile(id, authentication);
    }

    @PutMapping("/{id}")
    public CandidateResponse updateProfile(
            @PathVariable Long id, @RequestBody CandidateUpdateRequest request, Authentication authentication) {
        return candidateService.updateProfile(id, request, authentication);
    }

    @PostMapping("/{id}/skills/{skillId}")
    public CandidateResponse addSkill(
            @PathVariable Long id, @PathVariable Long skillId, Authentication authentication) {
        return candidateService.addSkill(id, skillId, authentication);
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    public CandidateResponse removeSkill(
            @PathVariable Long id, @PathVariable Long skillId, Authentication authentication) {
        return candidateService.removeSkill(id, skillId, authentication);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable Long id, Authentication authentication) {
        candidateService.deleteProfile(id, authentication);
    }
}
