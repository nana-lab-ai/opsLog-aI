package com.example.opslogai.controller;

import com.example.opslogai.dto.WatchSettingForm;
import com.example.opslogai.entity.WatchSetting;
import com.example.opslogai.service.DirectoryWatchService;
import com.example.opslogai.service.WatchSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/settings/watch")
@RequiredArgsConstructor
public class WatchSettingController {

    private final WatchSettingService watchSettingService;
    private final DirectoryWatchService directoryWatchService;

    @GetMapping
    public String show(Model model) {
        WatchSetting setting = watchSettingService.getOrInit();
        model.addAttribute("setting", setting);
        model.addAttribute("form", new WatchSettingForm(setting));
        model.addAttribute("isRunning", directoryWatchService.isRunning());
        model.addAttribute("pageTitle", "監視設定");
        return "settings/watch";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("form") WatchSettingForm form,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Model model) {

        if (bindingResult.hasErrors()) {
            populateModel(model);
            return "settings/watch";
        }

        // 監視有効時: ディレクトリが存在しなければ自動作成
        if (form.isEnabled()) {
            Path dir = Path.of(form.getDirectoryPath().trim());
            if (!Files.isDirectory(dir)) {
                try {
                    Files.createDirectories(dir);
                    redirectAttributes.addFlashAttribute("infoMessage",
                            "監視ディレクトリが存在しなかったため、自動作成しました: " + dir.toAbsolutePath());
                } catch (IOException e) {
                    bindingResult.rejectValue("directoryPath", "error.directoryPath",
                            "ディレクトリを作成できませんでした: " + e.getMessage());
                    populateModel(model);
                    return "settings/watch";
                }
            }
        }

        WatchSetting saved = watchSettingService.save(form);
        // 再起動なしで監視サービスへ即時反映
        directoryWatchService.applySettings(saved);

        String statusMsg = saved.isEnabled() ? "監視サービスを起動しました。" : "監視サービスを停止しました。";
        redirectAttributes.addFlashAttribute("successMessage", "設定を保存しました。" + statusMsg);
        return "redirect:/settings/watch";
    }

    private void populateModel(Model model) {
        model.addAttribute("setting", watchSettingService.getOrInit());
        model.addAttribute("isRunning", directoryWatchService.isRunning());
        model.addAttribute("pageTitle", "監視設定");
    }
}
