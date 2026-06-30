package com.hireme.controller;

import com.hireme.dto.JobOfferResponse;
import com.hireme.service.JobOfferService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job-offers")
public class JobOfferController {

    @Autowired
    private JobOfferService jobOfferService;

    @GetMapping
    public List<JobOfferResponse> listOffers() {
        return jobOfferService.listAllForCandidates();
    }

    @GetMapping("/{id}")
    public JobOfferResponse getOffer(@PathVariable Long id) {
        return jobOfferService.getForCandidate(id);
    }
}
