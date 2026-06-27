package com.hireme.repository;

import com.hireme.model.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobOfferId(Long jobOfferId);

    List<Application> findByCandidateId(Long candidateId);
}
