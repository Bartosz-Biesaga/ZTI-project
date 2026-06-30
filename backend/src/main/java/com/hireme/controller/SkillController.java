package com.hireme.controller;

import com.hireme.dto.SkillRequest;
import com.hireme.dto.SkillResponse;
import com.hireme.service.SkillService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @GetMapping
    public List<SkillResponse> getSkills() {
        return skillService.findAll();
    }

    @GetMapping("/{id}")
    public SkillResponse getSkill(@PathVariable Long id) {
        return skillService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse createSkill(@RequestBody SkillRequest request) {
        return skillService.create(request);
    }

    @PutMapping("/{id}")
    public SkillResponse updateSkill(@PathVariable Long id, @RequestBody SkillRequest request) {
        return skillService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@PathVariable Long id) {
        skillService.delete(id);
    }
}
