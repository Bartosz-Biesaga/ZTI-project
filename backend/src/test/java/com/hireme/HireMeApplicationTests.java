package com.hireme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hireme.model.entity.Application;
import com.hireme.model.entity.Candidate;
import com.hireme.model.entity.Company;
import com.hireme.model.entity.JobOffer;
import com.hireme.model.entity.Skill;
import com.hireme.model.entity.User;
import com.hireme.model.enums.ApplicationStatus;
import com.hireme.model.enums.Role;
import com.hireme.repository.ApplicationRepository;
import com.hireme.repository.CandidateRepository;
import com.hireme.repository.CompanyRepository;
import com.hireme.repository.JobOfferRepository;
import com.hireme.repository.SkillRepository;
import com.hireme.repository.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class HireMeApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("hireme")
                    .withUsername("hireme")
                    .withPassword("hireme");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private UserRepository userRepository;
    @Autowired private CandidateRepository candidateRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private JobOfferRepository jobOfferRepository;
    @Autowired private ApplicationRepository applicationRepository;

    @Test
    void contextLoads() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    void persistsCoreEntitiesAndEnforcesUniqueApplicationPerOffer() {
        User candidateUser = new User();
        candidateUser.setEmail("candidate@example.com");
        candidateUser.setPasswordHash("hash");
        candidateUser.setRole(Role.CANDIDATE);
        candidateUser = userRepository.save(candidateUser);

        Candidate candidate = new Candidate();
        candidate.setUser(candidateUser);
        candidate.setFirstName("Jan");
        candidate.setLastName("Kowalski");
        candidate.setEmail("candidate@example.com");
        candidate = candidateRepository.save(candidate);

        User companyUser = new User();
        companyUser.setEmail("company@example.com");
        companyUser.setPasswordHash("hash");
        companyUser.setRole(Role.COMPANY);
        companyUser = userRepository.save(companyUser);

        Company company = new Company();
        company.setUser(companyUser);
        company.setName("Acme Corp");
        company = companyRepository.save(company);

        Skill skill = new Skill();
        skill.setName("Java");
        skill = skillRepository.save(skill);
        candidate.getSkills().add(skill);
        candidateRepository.save(candidate);

        JobOffer offer = new JobOffer();
        offer.setCompany(company);
        offer.setTitle("Backend Developer");
        offer.setDescription("Spring Boot role");
        offer.setMinSalary(8000.0);
        offer.setMaxSalary(12000.0);
        offer.setSkills(Set.of(skill));
        offer = jobOfferRepository.save(offer);

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJobOffer(offer);
        application.setStatus(ApplicationStatus.NEW);
        application = applicationRepository.save(application);

        assertThat(application.getId()).isNotNull();
        assertThat(applicationRepository.findByJobOfferId(offer.getId())).hasSize(1);
        assertThat(applicationRepository.findByCandidateId(candidate.getId())).hasSize(1);

        Application duplicate = new Application();
        duplicate.setCandidate(candidate);
        duplicate.setJobOffer(offer);
        duplicate.setStatus(ApplicationStatus.NEW);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> applicationRepository.saveAndFlush(duplicate));
    }
}
