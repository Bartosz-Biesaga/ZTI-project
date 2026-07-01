package com.hireme.service;

import com.hireme.dto.JobOfferRequest;
import com.hireme.dto.JobOfferResponse;
import com.hireme.dto.SkillResponse;
import com.hireme.model.entity.Candidate;
import com.hireme.model.entity.Company;
import com.hireme.model.entity.JobOffer;
import com.hireme.model.entity.Skill;
import com.hireme.repository.ApplicationRepository;
import com.hireme.repository.JobOfferRepository;
import com.hireme.repository.SkillRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobOfferService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MatchService matchService;

    public List<JobOfferResponse> listByCompany(Long companyId, Authentication authentication) {
        currentUserService.requireOwnedCompany(companyId, authentication);
        return jobOfferRepository.findByCompany_Id(companyId).stream()
                .sorted(Comparator.comparing(JobOffer::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    public JobOfferResponse create(Long companyId, JobOfferRequest request, Authentication authentication) {
        Company company = currentUserService.requireOwnedCompany(companyId, authentication);
        validateRequest(request);

        JobOffer offer = new JobOffer();
        offer.setCompany(company);
        applyRequest(offer, request);
        offer.setCreatedAt(LocalDateTime.now());
        return toResponse(jobOfferRepository.save(offer));
    }

    public JobOfferResponse getForCompany(Long companyId, Long offerId, Authentication authentication) {
        return toResponse(requireCompanyOffer(companyId, offerId, authentication));
    }

    public JobOfferResponse update(
            Long companyId, Long offerId, JobOfferRequest request, Authentication authentication) {
        JobOffer offer = requireCompanyOffer(companyId, offerId, authentication);
        validateRequest(request);
        applyRequest(offer, request);
        return toResponse(jobOfferRepository.save(offer));
    }

    @Transactional
    public void delete(Long companyId, Long offerId, Authentication authentication) {
        JobOffer offer = requireCompanyOffer(companyId, offerId, authentication);
        applicationRepository.deleteAll(applicationRepository.findByJobOfferId(offer.getId()));
        jobOfferRepository.delete(offer);
    }

    @Transactional(readOnly = true)
    public List<JobOfferResponse> listAllForCandidates(Authentication authentication, String sort) {
        validateSortParam(sort);

        List<JobOffer> offers = jobOfferRepository.findAll();

        if (!"match".equals(sort)) {
            return offers.stream()
                    .sorted(Comparator.comparing(
                            JobOffer::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .map(this::toResponse)
                    .toList();
        }

        Candidate candidate = currentUserService.requireAuthenticatedCandidate(authentication);

        return offers.stream()
                .map(offer -> {
                    JobOfferResponse response = toResponse(offer);
                    int percent = matchService.calculateMatchPercent(candidate.getSkills(), offer.getSkills());
                    response.setMatchPercent(percent);
                    return response;
                })
                .sorted(Comparator.comparing(
                                JobOfferResponse::getMatchPercent, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(
                                JobOfferResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Transactional(readOnly = true)
    public JobOfferResponse getForCandidate(Long offerId, Authentication authentication) {
        JobOffer offer = jobOfferRepository
                .findById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono oferty"));
        JobOfferResponse response = toResponse(offer);
        Candidate candidate = currentUserService.requireAuthenticatedCandidate(authentication);
        response.setMatchPercent(matchService.calculateMatchPercent(candidate.getSkills(), offer.getSkills()));
        return response;
    }

    private void validateSortParam(String sort) {
        if (sort != null && !sort.isBlank() && !"match".equals(sort)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowy sposób sortowania");
        }
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

    private void validateRequest(JobOfferRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tytuł oferty jest wymagany");
        }
        if (request.getMinSalary() != null
                && request.getMaxSalary() != null
                && request.getMinSalary() > request.getMaxSalary()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Minimalne wynagrodzenie nie może być większe od maksymalnego");
        }
    }

    private void applyRequest(JobOffer offer, JobOfferRequest request) {
        offer.setTitle(request.getTitle().trim());
        offer.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        offer.setMinSalary(request.getMinSalary());
        offer.setMaxSalary(request.getMaxSalary());
        offer.setSkills(resolveSkills(request.getSkillIds()));
    }

    private List<Skill> resolveSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(skillIds);
        List<Skill> skills = new ArrayList<>();
        for (Long skillId : uniqueIds) {
            Skill skill = skillRepository
                    .findById(skillId)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono umiejętności"));
            skills.add(skill);
        }
        return skills;
    }

    private JobOfferResponse toResponse(JobOffer offer) {
        List<SkillResponse> skills = offer.getSkills().stream()
                .map(s -> new SkillResponse(s.getId(), s.getName()))
                .toList();

        JobOfferResponse response = new JobOfferResponse();
        response.setId(offer.getId());
        response.setTitle(offer.getTitle());
        response.setDescription(offer.getDescription());
        response.setMinSalary(offer.getMinSalary());
        response.setMaxSalary(offer.getMaxSalary());
        response.setCreatedAt(offer.getCreatedAt());
        response.setCompanyId(offer.getCompany().getId());
        response.setCompanyName(offer.getCompany().getName());
        response.setSkills(skills);
        return response;
    }
}
