package com.hireme.repository;

import com.hireme.model.entity.JobOffer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    List<JobOffer> findByCompany_Id(Long companyId);
}
