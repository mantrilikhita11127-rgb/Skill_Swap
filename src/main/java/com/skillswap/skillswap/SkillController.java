
package com.skillswap.skillswap;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SkillController {

    private final SkillRepository skillRepository;
    private final AppUserRepository userRepository;

    public SkillController(
            SkillRepository skillRepository,
            AppUserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/skills")
    public String showSkills(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Skill> skills = skillRepository.findByUser_Id(userId);

        model.addAttribute("skills", skills);
        model.addAttribute("userName", session.getAttribute("userName"));

        return "skills";
    }

    @PostMapping("/skills")
    public String addSkill(
            @RequestParam String skillName,
            @RequestParam String skillType,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        String cleanedName = skillName.trim();

        if (cleanedName.isBlank() || cleanedName.length() > 100) {
            return "redirect:/skills?error";
        }

        if (!skillType.equals("TEACH") && !skillType.equals("LEARN")) {
            return "redirect:/skills?error";
        }

        AppUser user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Skill skill = new Skill();
        skill.setSkillName(cleanedName);
        skill.setSkillType(skillType);
        skill.setUser(user);

        skillRepository.save(skill);

        return "redirect:/skills?success";
    }

    @PostMapping("/skills/delete/{id}")
    public String deleteSkill(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        Skill skill = skillRepository.findById(id).orElse(null);

        if (skill != null && skill.getUser().getId().equals(userId)) {
            skillRepository.delete(skill);
        }

        return "redirect:/skills";
    }
}
