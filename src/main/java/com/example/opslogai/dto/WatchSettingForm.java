package com.example.opslogai.dto;

import com.example.opslogai.entity.WatchSetting;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchSettingForm {

    private boolean enabled;

    @NotBlank(message = "監視ディレクトリを入力してください")
    private String directoryPath;

    @NotBlank(message = "対象拡張子を入力してください（例: .log,.txt）")
    private String fileExtensions;

    public WatchSettingForm(WatchSetting setting) {
        this.enabled = setting.isEnabled();
        this.directoryPath = setting.getDirectoryPath();
        this.fileExtensions = setting.getFileExtensions();
    }
}
