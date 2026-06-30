package com.hireme.dto;

import com.hireme.model.enums.ApplicationStatus;
import java.time.LocalDateTime;

public class ApplicationResponse {

    private Long id;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String companyNotes;
    private Long jobOfferId;
    private String offerTitle;
    private String companyName;
    private Long candidateId;
    private String candidateFirstName;
    private String candidateLastName;
    private String candidateEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getCompanyNotes() {
        return companyNotes;
    }

    public void setCompanyNotes(String companyNotes) {
        this.companyNotes = companyNotes;
    }

    public Long getJobOfferId() {
        return jobOfferId;
    }

    public void setJobOfferId(Long jobOfferId) {
        this.jobOfferId = jobOfferId;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateFirstName() {
        return candidateFirstName;
    }

    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }

    public String getCandidateLastName() {
        return candidateLastName;
    }

    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
}
