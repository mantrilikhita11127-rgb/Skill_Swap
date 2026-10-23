
package com.skillswap.skillswap;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LearningSessionController {

    private final LearningSessionRepository sessionRepository;
    private final SkillConnectionRepository connectionRepository;
    private final AppUserRepository userRepository;

    public LearningSessionController(
            LearningSessionRepository sessionRepository,
            SkillConnectionRepository connectionRepository,
            AppUserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/sessions")
    public String showSessions(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<SkillConnection> connections =
                connectionRepository
                        .findByRequester_IdOrRecipient_Id(userId, userId);

        List<AppUser> partners = new ArrayList<>();

        for (SkillConnection connection : connections) {
            if (connection.getStatus() != ConnectionStatus.ACCEPTED) {
                continue;
            }

            AppUser partner;

            if (connection.getRequester().getId().equals(userId)) {
                partner = connection.getRecipient();
            } else {
                partner = connection.getRequester();
            }

            partners.add(partner);
        }

        List<LearningSession> sessions =
                sessionRepository
                        .findByRequester_IdOrRecipient_IdOrderByStartTimeAsc(
                                userId, userId);

        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("currentUserId", userId);
        model.addAttribute("partners", partners);
        model.addAttribute("sessions", sessions);

        return "sessions";
    }

    @PostMapping("/sessions")
    public String scheduleSession(
            @RequestParam(required = false) Long recipientId,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) Integer durationMinutes,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        if (recipientId == null
                || topic == null
                || startTime == null
                || durationMinutes == null) {
            return "redirect:/sessions?error";
        }

        String cleanedTopic = topic.trim();

        if (cleanedTopic.isBlank() || cleanedTopic.length() > 120) {
            return "redirect:/sessions?error";
        }

        if (!List.of(30, 45, 60).contains(durationMinutes)) {
            return "redirect:/sessions?error";
        }

        if (userId.equals(recipientId)) {
            return "redirect:/sessions?error";
        }

        LocalDateTime scheduledTime;

        try {
            scheduledTime = LocalDateTime.parse(startTime);
        } catch (Exception exception) {
            return "redirect:/sessions?error";
        }

        if (!scheduledTime.isAfter(LocalDateTime.now())) {
            return "redirect:/sessions?past";
        }

        AppUser requester = userRepository.findById(userId).orElse(null);
        AppUser recipient = userRepository.findById(recipientId).orElse(null);

        if (requester == null || recipient == null) {
            return "redirect:/sessions?error";
        }

        List<SkillConnection> connections =
                connectionRepository
                        .findByRequester_IdOrRecipient_Id(userId, userId);

        boolean isConnected = connections.stream().anyMatch(connection ->
                connection.getStatus() == ConnectionStatus.ACCEPTED
                &&
                (
                    (connection.getRequester().getId().equals(userId)
                        && connection.getRecipient().getId().equals(recipientId))
                    ||
                    (connection.getRequester().getId().equals(recipientId)
                        && connection.getRecipient().getId().equals(userId))
                )
        );

        if (!isConnected) {
            return "redirect:/sessions?notconnected";
        }

        LearningSession learningSession = new LearningSession();
        learningSession.setRequester(requester);
        learningSession.setRecipient(recipient);
        learningSession.setTopic(cleanedTopic);
        learningSession.setStartTime(scheduledTime);
        learningSession.setDurationMinutes(durationMinutes);
        learningSession.setStatus(SessionStatus.PENDING);

        sessionRepository.save(learningSession);

        return "redirect:/sessions?sent";
    }

    @PostMapping("/sessions/{id}/confirm")
    public String confirmSession(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        LearningSession learningSession =
                sessionRepository.findById(id).orElse(null);

        if (learningSession != null
                && learningSession.getRecipient().getId().equals(userId)
                && learningSession.getStatus() == SessionStatus.PENDING
                && learningSession.getStartTime().isAfter(LocalDateTime.now())) {

            learningSession.setStatus(SessionStatus.CONFIRMED);
            sessionRepository.save(learningSession);
        }

        return "redirect:/sessions?updated";
    }

    @PostMapping("/sessions/{id}/reject")
    public String rejectSession(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        LearningSession learningSession =
                sessionRepository.findById(id).orElse(null);

        if (learningSession != null
                && learningSession.getRecipient().getId().equals(userId)
                && learningSession.getStatus() == SessionStatus.PENDING) {

            learningSession.setStatus(SessionStatus.REJECTED);
            sessionRepository.save(learningSession);
        }

        return "redirect:/sessions?updated";
    }
}
