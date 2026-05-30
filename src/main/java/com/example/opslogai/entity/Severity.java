package com.example.opslogai.entity;

public enum Severity {
    CRITICAL("緊急"),
    HIGH("高"),
    MEDIUM("中"),
    LOW("低");

    private final String displayName;

    Severity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
