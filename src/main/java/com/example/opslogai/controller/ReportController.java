package com.example.opslogai.controller;

import com.example.opslogai.entity.Incident;
import com.example.opslogai.entity.ReportDraft;
import com.example.opslogai.service.IncidentService;
import com.example.opslogai.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/incidents/{id}/report")
@RequiredArgsConstructor
public class ReportController {

    private final IncidentService incidentService;
    private final ReportService reportService;

    @PostMapping("/generate")
    public String generate(@PathVariable Long id) {
        Incident incident = incidentService.findById(id);
        reportService.generateAndSave(incident);
        return "redirect:/incidents/" + id + "/report";
    }

    @GetMapping
    public String view(@PathVariable Long id, Model model) {
        Incident incident = incidentService.findById(id);
        Optional<ReportDraft> draft = reportService.findLatest(id);
        model.addAttribute("incident", incident);
        model.addAttribute("draft", draft.orElse(null));
        model.addAttribute("pageTitle", "障害報告書 - " + incident.getTitle());
        return "report/view";
    }
}
