package com.example.opslogai.controller;

import com.example.opslogai.dto.IncidentForm;
import com.example.opslogai.entity.*;
import com.example.opslogai.repository.LogEntryRepository;
import com.example.opslogai.repository.UserRepository;
import com.example.opslogai.service.AiAnalysisService;
import com.example.opslogai.service.IncidentService;
import com.example.opslogai.service.LogAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final AiAnalysisService aiAnalysisService;
    private final LogAnalysisService logAnalysisService;

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        List<Incident> incidents;
        if (status != null && !status.isBlank()) {
            try {
                IncidentStatus s = IncidentStatus.valueOf(status);
                incidents = incidentService.findByStatus(s);
                model.addAttribute("selectedStatus", s);
            } catch (IllegalArgumentException e) {
                incidents = incidentService.findAll();
            }
        } else {
            incidents = incidentService.findAll();
        }
        model.addAttribute("incidents", incidents);
        model.addAttribute("statuses", IncidentStatus.values());
        model.addAttribute("pageTitle", "障害一覧");
        return "incident/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long logEntryId, Model model) {
        IncidentForm form = new IncidentForm();

        if (logEntryId != null) {
            logEntryRepository.findById(logEntryId).ifPresent(entry -> {
                form.setLogEntryId(logEntryId);
                form.setTitle(entry.getTitle());
                String extractedLog = entry.getExtractedLog();
                if (extractedLog != null && !extractedLog.isBlank()) {
                    List<String> lines = Arrays.asList(extractedLog.split("\n"));
                    form.setAiSummary(aiAnalysisService.generateSummary(lines));
                    form.setAiCause(aiAnalysisService.generateCause(lines));
                    form.setAiAction(aiAnalysisService.generateAction(lines));
                }
                model.addAttribute("logEntry", entry);
            });
        }

        model.addAttribute("form", form);
        model.addAttribute("severities", Severity.values());
        model.addAttribute("statuses", IncidentStatus.values());
        model.addAttribute("pageTitle", "障害チケット作成");
        return "incident/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") IncidentForm form,
                          BindingResult result,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("severities", Severity.values());
            model.addAttribute("statuses", IncidentStatus.values());
            model.addAttribute("pageTitle", "障害チケット作成");
            return "incident/create";
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Incident incident = incidentService.createIncident(form, user);
        return "redirect:/incidents/" + incident.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Incident incident = incidentService.findById(id);
        model.addAttribute("incident", incident);
        model.addAttribute("statuses", IncidentStatus.values());
        model.addAttribute("pageTitle", incident.getTitle());
        return "incident/detail";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                              @RequestParam String comment,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        incidentService.addComment(id, comment, user);
        return "redirect:/incidents/" + id + "#comments";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam String status) {
        IncidentStatus newStatus = IncidentStatus.valueOf(status);
        incidentService.updateStatus(id, newStatus);
        return "redirect:/incidents/" + id;
    }
}
