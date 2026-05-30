package com.example.opslogai.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatSearchResult {
    private String type;            // "incident" or "log"
    private String linkUrl;         // e.g. "/incidents/1" or "/log/result/1"
    private String title;
    private String statusDisplay;   // e.g. "未対応" (null for logs)
    private String severityDisplay; // e.g. "緊急" (null for logs)
    private String statusCss;       // CSS class for status badge
    private String severityCss;     // CSS class for severity badge
    private String summary;         // brief description snippet
    private String matchedField;    // e.g. "タイトル", "AI解析", "コメント", "報告書", "ログ内容"
}
