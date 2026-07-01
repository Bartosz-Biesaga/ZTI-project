package com.hireme.repository;

import com.hireme.model.entity.Candidate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByUser_Id(Long userId);

    Optional<Candidate> findByEmail(String email);
}
