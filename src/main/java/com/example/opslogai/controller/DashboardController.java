package com.example.opslogai.controller;

import com.example.opslogai.entity.IncidentStatus;
import com.example.opslogai.repository.LogEntryRepository;
import com.example.opslogai.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final IncidentService incidentService;
    private final LogEntryRepository logEntryRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalIncidents", incidentService.findAll().size());
        model.addAttribute("openCount", incidentService.countByStatus(IncidentStatus.OPEN));
        model.addAttribute("investigatingCount", incidentService.countByStatus(IncidentStatus.INVESTIGATING));
        model.addAttribute("resolvedCount", incidentService.countByStatus(IncidentStatus.RESOLVED));
        model.addAttribute("onHoldCount", incidentService.countByStatus(IncidentStatus.ON_HOLD));
        model.addAttribute("recentIncidents", incidentService.findAll().stream().limit(5).toList());
        model.addAttribute("recentLogs", logEntryRepository.findAllByOrderByCreatedAtDesc().stream().limit(5).toList());
        model.addAttribute("pageTitle", "ダッシュボード");
        return "dashboard";
    }
}
