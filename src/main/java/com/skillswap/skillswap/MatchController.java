
package com.skillswap.skillswap;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchController {

    private final SkillRepository skillRepository;

    public MatchController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping("/matches")
    public String showMatches(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Skill> mySkills = skillRepository.findByUser_Id(userId);

        List<String> wantedSkills = mySkills.stream()
                .filter(skill -> "LEARN".equalsIgnoreCase(skill.getSkillType()))
                .map(skill -> skill.getSkillName().trim().toLowerCase(Locale.ROOT))
                .toList();

        List<Skill> matches = skillRepository
                .findBySkillTypeIgnoreCase("TEACH")
                .stream()
                .filter(skill -> !skill.getUser().getId().equals(userId))
                .filter(skill -> wantedSkills.contains(
                        skill.getSkillName().trim().toLowerCase(Locale.ROOT)))
                .toList();

        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("matches", matches);

        return "matches";
    }
}
