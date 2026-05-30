package com.example.opslogai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogAnalysisRequest {

    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 200, message = "タイトルは200文字以内で入力してください")
    private String title;

    @NotBlank(message = "ログを入力してください")
    @Size(max = 30_000, message = "ログ本文が長すぎます。デモ環境では30,000文字以内で入力してください。")
    private String rawLog;
}
