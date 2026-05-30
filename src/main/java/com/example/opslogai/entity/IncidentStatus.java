package com.example.opslogai.entity;

public enum IncidentStatus {
    OPEN("未対応"),
    INVESTIGATING("調査中"),
    RESOLVED("対応済み"),
    ON_HOLD("保留");

    private final String displayName;

    IncidentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
