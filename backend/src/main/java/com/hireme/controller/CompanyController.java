package com.hireme.controller;

import com.hireme.dto.CompanyResponse;
import com.hireme.dto.CompanyUpdateRequest;
import com.hireme.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/{id}")
    public CompanyResponse getProfile(@PathVariable Long id, Authentication authentication) {
        return companyService.getProfile(id, authentication);
    }

    @PutMapping("/{id}")
    public CompanyResponse updateProfile(
            @PathVariable Long id, @RequestBody CompanyUpdateRequest request, Authentication authentication) {
        return companyService.updateProfile(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable Long id, Authentication authentication) {
        companyService.deleteProfile(id, authentication);
    }
}
