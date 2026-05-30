package com.example.opslogai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "opslog.watch")
@Getter
@Setter
public class WatchProperties {

    /** 監視機能の有効/無効 */
    private boolean enabled = false;

    /** 監視対象ディレクトリの絶対パス */
    private String directory = "";

    /** 取り込み対象の拡張子リスト */
    private List<String> fileExtensions = List.of(".log", ".txt");
}
