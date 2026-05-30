package com.example.opslogai.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 全コントローラーにレート制限エラーメッセージを注入する。
 * RateLimitInterceptor がセッションにセットした rateLimitError を読み取り、
 * 使い終わったら削除する（ワンショットフラッシュ相当）。
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ModelAttribute("rateLimitError")
    public String injectRateLimitError(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        String error = (String) session.getAttribute("rateLimitError");
        if (error != null) {
            session.removeAttribute("rateLimitError");
        }
        return error;
    }
}
