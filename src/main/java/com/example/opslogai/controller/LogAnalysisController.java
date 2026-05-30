package com.example.opslogai.controller;

import com.example.opslogai.dto.LogAnalysisRequest;
import com.example.opslogai.dto.LogAnalysisResult;
import com.example.opslogai.entity.LogEntry;
import com.example.opslogai.entity.User;
import com.example.opslogai.exception.DemoDataLimitException;
import com.example.opslogai.repository.LogEntryRepository;
import com.example.opslogai.repository.UserRepository;
import com.example.opslogai.service.LogAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogAnalysisController {

    private final LogAnalysisService logAnalysisService;
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;

    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("request", new LogAnalysisRequest());
        model.addAttribute("pageTitle", "ログ解析");
        return "log/input";
    }

    @PostMapping("/analyze")
    public String analyze(@Valid @ModelAttribute("request") LogAnalysisRequest request,
                           BindingResult result,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "ログ解析");
            return "log/input";
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        try {
            LogAnalysisResult analysisResult = logAnalysisService.analyzeAndSave(request, user);
            return "redirect:/log/result/" + analysisResult.getLogEntryId();
        } catch (DemoDataLimitException e) {
            model.addAttribute("limitError", e.getMessage());
            model.addAttribute("pageTitle", "ログ解析");
            return "log/input";
        }
    }

    @GetMapping("/result/{id}")
    public String result(@PathVariable Long id, Model model) {
        LogEntry entry = logEntryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ログエントリが見つかりません: " + id));

        String extractedLog = entry.getExtractedLog();
        java.util.List<String> extractedLines = (extractedLog != null && !extractedLog.isBlank())
                ? java.util.Arrays.asList(extractedLog.split("\n"))
                : java.util.List.of();

        model.addAttribute("entry", entry);
        model.addAttribute("extractedLines", extractedLines);
        model.addAttribute("totalLines", entry.getRawLog().split("\n").length);
        model.addAttribute("extractedCount", extractedLines.size());
        model.addAttribute("pageTitle", "解析結果");
        return "log/result";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("entries", logEntryRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("pageTitle", "ログ履歴");
        return "log/list";
    }
}
