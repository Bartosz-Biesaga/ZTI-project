package com.hireme.service;

import com.hireme.dto.ApplicationCreateRequest;
import com.hireme.dto.ApplicationResponse;
import com.hireme.dto.ApplicationUpdateRequest;
import com.hireme.model.entity.Application;
import com.hireme.model.entity.Candidate;
import com.hireme.model.entity.JobOffer;
import com.hireme.model.enums.ApplicationStatus;
import com.hireme.repository.ApplicationRepository;
import com.hireme.repository.JobOfferRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ApplicationService {

    private static final DateTimeFormatter NOTE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public ApplicationResponse apply(
            Long candidateId, ApplicationCreateRequest request, Authentication authentication) {
        Candidate candidate = currentUserService.requireOwnedCandidate(candidateId, authentication);

        if (request.getJobOfferId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID oferty jest wymagane");
        }

        JobOffer offer = jobOfferRepository
                .findById(request.getJobOfferId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono oferty"));

        if (applicationRepository.existsByCandidateIdAndJobOfferId(candidate.getId(), offer.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Już aplikowałeś na tę ofertę");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJobOffer(offer);
        application.setStatus(ApplicationStatus.NEW);
        application.setAppliedAt(LocalDateTime.now());

        return toCandidateResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> listForCandidate(Long candidateId, Authentication authentication) {
        currentUserService.requireOwnedCandidate(candidateId, authentication);
        return applicationRepository.findByCandidateId(candidateId).stream()
                .sorted(Comparator.comparing(Application::getAppliedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toCandidateResponse)
                .toList();
    }

    public List<ApplicationResponse> listForOffer(
            Long companyId, Long offerId, Authentication authentication) {
        requireCompanyOffer(companyId, offerId, authentication);
        return applicationRepository.findByJobOfferId(offerId).stream()
                .sorted(Comparator.comparing(Application::getAppliedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toCompanyResponse)
                .toList();
    }

    public ApplicationResponse update(
            Long companyId,
            Long offerId,
            Long applicationId,
            ApplicationUpdateRequest request,
            Authentication authentication) {
        requireCompanyOffer(companyId, offerId, authentication);

        Application application = applicationRepository
                .findByIdAndJobOfferId(applicationId, offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono aplikacji"));

        boolean hasStatus = request.getStatus() != null;
        boolean hasNote = request.getNote() != null && !request.getNote().isBlank();

        if (!hasStatus && !hasNote) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Podaj status lub notatkę do zaktualizowania");
        }

        if (hasStatus) {
            application.setStatus(request.getStatus());
        }

        if (hasNote) {
            ApplicationStatus stage = application.getStatus();
            String entry = String.format(
                    "[%s %s] %s%n",
                    LocalDateTime.now().format(NOTE_DATE_FORMAT),
                    stage.name(),
                    request.getNote().trim());
            String existing = application.getCompanyNotes();
            application.setCompanyNotes(existing == null ? entry : existing + entry);
        }

        return toCompanyResponse(applicationRepository.save(application));
    }

    private JobOffer requireCompanyOffer(Long companyId, Long offerId, Authentication authentication) {
        currentUserService.requireOwnedCompany(companyId, authentication);
        JobOffer offer = jobOfferRepository
                .findById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono oferty"));
        if (!offer.getCompany().getId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono oferty");
        }
        return offer;
    }

    private ApplicationResponse toCandidateResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        response.setJobOfferId(application.getJobOffer().getId());
        response.setOfferTitle(application.getJobOffer().getTitle());
        response.setCompanyName(application.getJobOffer().getCompany().getName());
        return response;
    }

    private ApplicationResponse toCompanyResponse(Application application) {
        ApplicationResponse response = toCandidateResponse(application);
        Candidate candidate = application.getCandidate();
        response.setCandidateId(candidate.getId());
        response.setCandidateFirstName(candidate.getFirstName());
        response.setCandidateLastName(candidate.getLastName());
        response.setCandidateEmail(candidate.getEmail());
        response.setCompanyNotes(application.getCompanyNotes());
        return response;
    }
}
