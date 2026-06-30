package com.hireme.service;

import com.hireme.dto.SkillRequest;
import com.hireme.dto.SkillResponse;
import com.hireme.model.entity.Skill;
import com.hireme.repository.SkillRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public List<SkillResponse> findAll() {
        return skillRepository.findAll().stream()
                .sorted(Comparator.comparing(Skill::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponse)
                .toList();
    }

    public SkillResponse findById(Long id) {
        Skill skill = skillRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono umiejętności"));
        return toResponse(skill);
    }

    public SkillResponse create(SkillRequest request) {
        validateName(request.getName(), null);
        Skill skill = new Skill();
        skill.setName(request.getName().trim());
        return toResponse(skillRepository.save(skill));
    }

    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = skillRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono umiejętności"));
        validateName(request.getName(), id);
        skill.setName(request.getName().trim());
        return toResponse(skillRepository.save(skill));
    }

    public void delete(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono umiejętności");
        }
        skillRepository.deleteById(id);
    }

    private void validateName(String name, Long excludeId) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nazwa umiejętności jest wymagana");
        }
        if (name.trim().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nazwa umiejętności może mieć maksymalnie 50 znaków");
        }
        skillRepository.findByName(name.trim()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ta umiejętność już istnieje");
            }
        });
    }

    private SkillResponse toResponse(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName());
    }
}
