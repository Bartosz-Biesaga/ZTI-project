package com.hireme.repository;

import com.hireme.model.entity.Application;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobOfferId(Long jobOfferId);

    List<Application> findByCandidateId(Long candidateId);

    boolean existsByCandidateIdAndJobOfferId(Long candidateId, Long jobOfferId);

    Optional<Application> findByIdAndJobOfferId(Long id, Long jobOfferId);
}
