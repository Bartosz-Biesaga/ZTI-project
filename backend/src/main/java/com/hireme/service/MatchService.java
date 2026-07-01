package com.hireme.service;

import com.hireme.model.entity.Skill;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
    public int calculateMatchPercent(List<Skill> candidateSkills, List<Skill> offerSkills) {
        if (offerSkills == null || offerSkills.isEmpty()) {
            return 100;
        }

        Set<Long> candidateSkillIds = new HashSet<>();
        if (candidateSkills != null) {
            for (Skill skill : candidateSkills) {
                candidateSkillIds.add(skill.getId());
            }
        }

        int matched = 0;
        for (Skill offerSkill : offerSkills) {
            if (candidateSkillIds.contains(offerSkill.getId())) {
                matched++;
            }
        }

        return Math.round((float) matched / offerSkills.size() * 100);
    }
}
