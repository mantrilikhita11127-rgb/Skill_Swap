package com.skillswap.skillswap;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConnectionController {

    private final SkillConnectionRepository connectionRepository;
    private final AppUserRepository userRepository;

    public ConnectionController(
            SkillConnectionRepository connectionRepository,
            AppUserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/connections")
    public String showConnections(
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<SkillConnection> allConnections =
                connectionRepository
                        .findByRequester_IdOrRecipient_Id(userId, userId);

        List<SkillConnection> incoming = allConnections.stream()
                .filter(c -> c.getRecipient().getId().equals(userId))
                .filter(c -> c.getStatus() == ConnectionStatus.PENDING)
                .toList();

        List<SkillConnection> outgoing = allConnections.stream()
                .filter(c -> c.getRequester().getId().equals(userId))
                .filter(c -> c.getStatus() == ConnectionStatus.PENDING)
                .toList();

        List<SkillConnection> accepted = allConnections.stream()
                .filter(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                .toList();

        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("currentUserId", userId);
        model.addAttribute("incoming", incoming);
        model.addAttribute("outgoing", outgoing);
        model.addAttribute("accepted", accepted);

        return "connections";
    }

    @PostMapping("/connections/request/{targetUserId}")
    public String sendRequest(
            @PathVariable Long targetUserId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        if (userId.equals(targetUserId)) {
            return "redirect:/matches?invalid";
        }

        AppUser requester = userRepository.findById(userId).orElse(null);
        AppUser recipient = userRepository.findById(targetUserId).orElse(null);

        if (requester == null || recipient == null) {
            return "redirect:/matches?invalid";
        }

        List<SkillConnection> existingConnections =
                connectionRepository
                        .findByRequester_IdOrRecipient_Id(userId, userId);

        boolean alreadyExists = existingConnections.stream().anyMatch(c ->
                (c.getRequester().getId().equals(userId)
                        && c.getRecipient().getId().equals(targetUserId))
                ||
                (c.getRequester().getId().equals(targetUserId)
                        && c.getRecipient().getId().equals(userId))
        );

        if (alreadyExists) {
            return "redirect:/connections?exists";
        }

        SkillConnection connection = new SkillConnection();
        connection.setRequester(requester);
        connection.setRecipient(recipient);
        connection.setStatus(ConnectionStatus.PENDING);

        connectionRepository.save(connection);

        return "redirect:/connections?sent";
    }

    @PostMapping("/connections/{id}/accept")
    public String acceptRequest(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        SkillConnection connection =
                connectionRepository.findById(id).orElse(null);

        if (connection != null
                && connection.getRecipient().getId().equals(userId)
                && connection.getStatus() == ConnectionStatus.PENDING) {

            connection.setStatus(ConnectionStatus.ACCEPTED);
            connectionRepository.save(connection);
        }

        return "redirect:/connections";
    }

    @PostMapping("/connections/{id}/reject")
    public String rejectRequest(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        SkillConnection connection =
                connectionRepository.findById(id).orElse(null);

        if (connection != null
                && connection.getRecipient().getId().equals(userId)
                && connection.getStatus() == ConnectionStatus.PENDING) {

            connection.setStatus(ConnectionStatus.REJECTED);
            connectionRepository.save(connection);
        }

        return "redirect:/connections";
    }
}