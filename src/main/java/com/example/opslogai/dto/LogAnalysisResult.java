package com.example.opslogai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LogAnalysisResult {
    private Long logEntryId;
    private String title;
    private List<String> extractedLines;
    private int totalLines;
    private int extractedCount;
    private String aiSummary;
    private String aiCause;
    private String aiAction;
}
