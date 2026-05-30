package com.example.opslogai.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * デモ公開用の簡易レート制限インターセプター。
 * 同一セッション＋IPから10秒以内の連続実行を制限する。
 */
@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final long RATE_LIMIT_MS = 10_000L;
    private static final int MAX_MAP_SIZE = 2000;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> RATE_LIMITED_PATTERNS = List.of(
            "/log/analyze",
            "/incidents",
            "/incidents/*/comments",
            "/incidents/*/report/generate",
            "/chat/search"
    );

    private final ConcurrentHashMap<String, Instant> lastRequestMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"POST".equals(request.getMethod())) return true;

        String path = request.getServletPath();
        if (!isRateLimited(path)) return true;

        String key = buildKey(request, path);
        Instant now = Instant.now();
        Instant last = lastRequestMap.get(key);

        if (last != null && Duration.between(last, now).toMillis() < RATE_LIMIT_MS) {
            log.warn("Rate limited: path={}, ip={}", path, getClientIp(request));
            handleRateLimited(request, response);
            return false;
        }

        // 古いエントリを定期的に除去してメモリリークを防ぐ
        if (lastRequestMap.size() >= MAX_MAP_SIZE) {
            lastRequestMap.entrySet().removeIf(e ->
                    Duration.between(e.getValue(), now).toMillis() > RATE_LIMIT_MS * 3);
        }

        lastRequestMap.put(key, now);
        return true;
    }

    private boolean isRateLimited(String path) {
        return RATE_LIMITED_PATTERNS.stream().anyMatch(p -> PATH_MATCHER.match(p, path));
    }

    private String buildKey(HttpServletRequest request, String path) {
        String ip = getClientIp(request);
        HttpSession session = request.getSession(false);
        String sessionFragment = session != null
                ? session.getId().substring(0, Math.min(session.getId().length(), 10))
                : "nosession";
        return ip + ":" + sessionFragment + ":" + normalizeOp(path);
    }

    private String normalizeOp(String path) {
        if ("/log/analyze".equals(path)) return "log_analyze";
        if ("/incidents".equals(path)) return "incident_create";
        if ("/chat/search".equals(path)) return "chat_search";
        if (PATH_MATCHER.match("/incidents/*/comments", path)) return "incident_comment";
        if (PATH_MATCHER.match("/incidents/*/report/generate", path)) return "report_generate";
        return path;
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void handleRateLimited(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String msg = "短時間に連続操作が行われました。少し待ってから再実行してください。";

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"message\":\"" + msg + "\",\"hasResults\":false,\"results\":[]}");
        } else {
            HttpSession session = request.getSession(true);
            session.setAttribute("rateLimitError", msg);
            String referer = request.getHeader("Referer");
            response.sendRedirect(referer != null ? referer : "/dashboard");
        }
    }
}
