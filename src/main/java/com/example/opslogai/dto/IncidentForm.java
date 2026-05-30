package com.example.opslogai.dto;

import com.example.opslogai.entity.IncidentStatus;
import com.example.opslogai.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidentForm {

    @NotBlank(message = "タイトルを入力してください")
    private String title;

    @NotNull(message = "重要度を選択してください")
    private Severity severity;

    private IncidentStatus status = IncidentStatus.OPEN;

    private Long logEntryId;

    private String aiSummary;
    private String aiCause;
    private String aiAction;
}
