package com.hireme.repository;

import com.hireme.model.entity.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUser_Id(Long userId);
}
