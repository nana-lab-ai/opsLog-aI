package com.example.opslogai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogAnalysisRequest {

    @NotBlank(message = "タイトルを入力してください")
    private String title;

    @NotBlank(message = "ログを入力してください")
    private String rawLog;
}
