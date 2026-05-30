package com.example.opslogai.controller;

import com.example.opslogai.dto.ChatRequest;
import com.example.opslogai.dto.ChatResponse;
import com.example.opslogai.entity.Incident;
import com.example.opslogai.entity.IncidentComment;
import com.example.opslogai.entity.LogEntry;
import com.example.opslogai.repository.LogEntryRepository;
import com.example.opslogai.service.ChatSearchService;
import com.example.opslogai.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSearchService chatSearchService;
    private final IncidentService incidentService;
    private final LogEntryRepository logEntryRepository;

    /** Chat page (initial load) */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "AIチャット検索");
        return "chat/index";
    }

    private static final int MAX_QUERY_LENGTH = 500;

    /**
     * Async search API — called by chat.js via fetch().
     * Returns JSON; CSRF token required in request header.
     *
     * Future: replace chatSearchService.search() body's generateMessage()
     *         with a Claude / OpenAI API call for natural language responses.
     */
    @PostMapping(value = "/search", produces = "application/json")
    @ResponseBody
    public ChatResponse search(@RequestBody ChatRequest request) {
        String query = request.getQuery() != null ? request.getQuery().trim() : "";
        if (query.length() > MAX_QUERY_LENGTH) {
            return ChatResponse.builder()
                    .message("質問文が長すぎます。" + MAX_QUERY_LENGTH + "文字以内で入力してください。")
                    .hasResults(false)
                    .results(List.of())
                    .build();
        }
        return chatSearchService.search(query);
    }

    /**
     * Incident detail fragment — injected into the right detail pane by chat.js.
     * Returns only the th:fragment="detail" portion of the template.
     */
    @GetMapping("/detail/incident/{id}")
    public String incidentDetail(@PathVariable Long id, Model model) {
        try {
            Incident incident = incidentService.findById(id);
            model.addAttribute("incident", incident);

            // Pass last 3 comments for the detail pane (avoid lazy-load issues)
            List<IncidentComment> comments = incident.getComments();
            int size = comments.size();
            model.addAttribute("recentComments",
                    comments.subList(Math.max(0, size - 3), size));
        } catch (Exception e) {
            model.addAttribute("detailError",
                    "障害情報（ID: " + id + "）が見つかりませんでした。");
        }
        return "chat/fragments/incident-detail :: detail";
    }

    /**
     * Log entry detail fragment — injected into the right detail pane by chat.js.
     */
    @GetMapping("/detail/log/{id}")
    public String logDetail(@PathVariable Long id, Model model) {
        LogEntry log = logEntryRepository.findById(id).orElse(null);
        if (log != null) {
            model.addAttribute("log", log);
        } else {
            model.addAttribute("detailError",
                    "ログエントリ（ID: " + id + "）が見つかりませんでした。");
        }
        return "chat/fragments/log-detail :: detail";
    }
}
