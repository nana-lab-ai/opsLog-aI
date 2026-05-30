package com.example.opslogai.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatResponse {
    private String message;               // natural language answer (replaceable with AI API)
    private List<ChatSearchResult> results;
    private boolean hasResults;
    private String query;                 // original query string
}
