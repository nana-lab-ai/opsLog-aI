package com.example.opslogai.config;

import com.example.opslogai.entity.*;
import com.example.opslogai.repository.*;
import com.example.opslogai.service.AiAnalysisService;
import com.example.opslogai.service.LogAnalysisService;
import com.example.opslogai.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LogEntryRepository logEntryRepository;
    private final IncidentRepository incidentRepository;
    private final IncidentCommentRepository commentRepository;
    private final ReportDraftRepository reportDraftRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogAnalysisService logAnalysisService;
    private final AiAnalysisService aiAnalysisService;
    private final ReportService reportService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // ddl-auto=update 時は2回目以降の起動でもこのメソッドが呼ばれるため、
        // 既存データがある場合はスキップして重複挿入を防ぐ
        if (userRepository.count() > 0) {
            log.info("既存データが存在します。サンプルデータの初期化をスキップします。");
            return;
        }
        log.info("サンプルデータを初期化しています...");

        User demo = new User();
        demo.setUsername("demo");
        demo.setPassword(passwordEncoder.encode("demo123"));
        demo.setRole("USER");
        demo.setCreatedAt(LocalDateTime.now().minusDays(90));
        demo = userRepository.save(demo);

        // === ログエントリ 1: APサーバー ヒープメモリ不足 ===
        String rawLog1 = """
2026-03-15 09:12:00,123 INFO  [main] c.e.app.AppServer - Application starting on port 8080
2026-03-15 09:15:34,456 WARN  [http-exec-2] c.e.app.cache.CacheManager - Cache memory usage exceeded 80%: 83%
2026-03-15 09:20:11,789 ERROR [http-exec-4] c.e.app.controller.OrderController - Failed to process order request for userId=10283
java.lang.NullPointerException: Cannot invoke method getOrderItems() on null reference
    at com.example.app.controller.OrderController.processOrder(OrderController.java:142)
    at com.example.app.controller.OrderController.handlePost(OrderController.java:87)
2026-03-15 09:45:23,012 WARN  [GC-Daemon] c.e.app.jvm.GCMonitor - GC overhead limit approaching: 92% heap used
2026-03-15 10:00:00,345 ERROR [http-exec-7] c.e.app.Application - Critical failure: Out of memory
java.lang.OutOfMemoryError: Java heap space
    at java.util.Arrays.copyOf(Arrays.java:3236)
    at com.example.app.service.ReportBatchService.generateReport(ReportBatchService.java:301)
    at com.example.app.service.ReportBatchService.runBatch(ReportBatchService.java:156)
2026-03-15 10:00:01,678 ERROR [main] c.e.app.Application - JVM shutting down due to OutOfMemoryError
2026-03-15 10:00:02,901 ERROR [shutdown-hook] c.e.app.AppServer - Failed to gracefully shutdown application
""";
        LogEntry log1 = createLogEntry(demo, "本番APサーバー ヒープメモリ不足によるアプリ停止", rawLog1,
                LocalDateTime.now().minusDays(75));

        // === ログエントリ 2: DBサーバー接続タイムアウト ===
        String rawLog2 = """
2026-04-02 13:00:00,000 INFO  [main] c.e.db.ConnectionPool - HikariCP connection pool initialized (maxPoolSize=10)
2026-04-02 14:22:31,123 WARN  [pool-thread-3] c.e.db.ConnectionPool - Connection pool usage: 80% (8/10)
2026-04-02 14:35:44,456 ERROR [pool-thread-1] c.e.db.ConnectionManager - Connection timeout after 30000ms waiting for available connection
java.sql.SQLTimeoutException: Timeout waiting for connection from pool after 30000ms
    at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:213)
    at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:100)
2026-04-02 14:35:45,789 ERROR [pool-thread-2] c.e.db.ConnectionManager - Connection timeout after 30000ms waiting for available connection
2026-04-02 14:35:46,012 ERROR [pool-thread-4] c.e.db.ConnectionManager - Connection refused by database server: db-primary.internal:5432
java.net.ConnectException: Connection refused
    at java.net.PlainSocketImpl.socketConnect(Native Method)
    at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
2026-04-02 14:36:00,345 WARN  [health-monitor] c.e.app.health.DBHealthCheck - Database health check FAILED: connection refused
2026-04-02 14:36:30,678 ERROR [main] c.e.app.Application - Failed to establish database connection after 3 retries. Service degraded.
""";
        LogEntry log2 = createLogEntry(demo, "DBサーバー接続タイムアウト多発・サービス断", rawLog2,
                LocalDateTime.now().minusDays(58));

        // === ログエントリ 3: 認証サービス障害 ===
        String rawLog3 = """
2026-04-28 11:00:00,000 INFO  [main] c.e.auth.AuthService - Authentication service started
2026-04-28 11:23:45,123 WARN  [auth-thread-1] c.e.auth.CertManager - SSL certificate expiry warning: expires in 3 days
2026-04-28 11:45:12,456 ERROR [auth-thread-2] c.e.auth.AuthController - Authentication failed for user: sysadmin@example.com
com.example.auth.exception.AuthenticationException: SSL certificate validation failed - certificate expired
    at com.example.auth.service.SslAuthService.validate(SslAuthService.java:89)
    at com.example.auth.controller.AuthController.authenticate(AuthController.java:134)
2026-04-28 11:45:13,789 ERROR [auth-thread-3] c.e.auth.AuthController - Access denied: invalid client certificate
2026-04-28 11:45:14,012 ERROR [auth-thread-4] c.e.auth.AuthController - Access denied: invalid client certificate
2026-04-28 12:00:00,345 ERROR [auth-thread-1] c.e.auth.AuthService - Authentication service: 47 failed requests in last 15 minutes
2026-04-28 12:00:01,678 WARN  [monitor] c.e.app.monitoring.AlertManager - ALERT: Authentication failure rate exceeded threshold (>30/min)
""";
        LogEntry log3 = createLogEntry(demo, "認証サービス SSL証明書期限切れによるアクセス拒否", rawLog3,
                LocalDateTime.now().minusDays(32));

        // === ログエントリ 4: バッチ処理警告 ===
        String rawLog4 = """
2026-05-20 02:00:00,000 INFO  [batch-main] c.e.batch.NightlyBatch - Nightly batch processing started
2026-05-20 02:15:23,123 WARN  [batch-thread-1] c.e.batch.DataAggregator - Slow query detected: 8234ms (threshold: 5000ms)
SELECT * FROM orders WHERE created_at > '2026-05-19' ORDER BY user_id
2026-05-20 02:30:45,456 WARN  [batch-thread-1] c.e.batch.DataAggregator - Slow query detected: 12456ms
SELECT * FROM order_items JOIN products ON order_items.product_id = products.id
2026-05-20 02:45:12,789 WARN  [batch-main] c.e.batch.NightlyBatch - Batch elapsed time exceeded: 45min (expected: 30min)
2026-05-20 03:00:00,012 WARN  [batch-main] c.e.batch.NightlyBatch - Batch processing timeout approaching: 60min limit
2026-05-20 03:15:34,345 ERROR [batch-main] c.e.batch.NightlyBatch - Batch processing timeout: exceeded maximum allowed time of 60 minutes
com.example.batch.exception.BatchTimeoutException: Batch execution timeout after 75 minutes
    at com.example.batch.runner.BatchRunner.execute(BatchRunner.java:201)
""";
        LogEntry log4 = createLogEntry(demo, "夜間バッチ処理タイムアウト・スロークエリ多発", rawLog4,
                LocalDateTime.now().minusDays(10));

        // === インシデント 1: CRITICAL / OPEN ===
        List<String> extracted1 = logAnalysisService.extractErrorLines(rawLog1);
        Incident inc1 = createIncident(demo, log1, "【緊急】本番APサーバー OOMによるサービス停止",
                IncidentStatus.OPEN, Severity.CRITICAL,
                aiAnalysisService.generateSummary(extracted1),
                aiAnalysisService.generateCause(extracted1),
                aiAnalysisService.generateAction(extracted1),
                LocalDateTime.now().minusDays(75));
        addComment(inc1, demo, "本番サーバーのAPプロセスがダウンしていることを確認。監視アラートを受信。\nヒープダンプの取得を試みているが、プロセスが応答していない。",
                LocalDateTime.now().minusDays(74).withHour(10).withMinute(15));

        // === インシデント 2: HIGH / INVESTIGATING ===
        List<String> extracted2 = logAnalysisService.extractErrorLines(rawLog2);
        Incident inc2 = createIncident(demo, log2, "本番DBサーバー 接続タイムアウト多発・サービス影響",
                IncidentStatus.INVESTIGATING, Severity.HIGH,
                aiAnalysisService.generateSummary(extracted2),
                aiAnalysisService.generateCause(extracted2),
                aiAnalysisService.generateAction(extracted2),
                LocalDateTime.now().minusDays(58));
        addComment(inc2, demo, "DBサーバー（db-primary.internal）へのping疎通は確認済み。\nポート5432への接続テストでタイムアウトが発生。DBサービス自体が応答していない可能性あり。",
                LocalDateTime.now().minusDays(57).withHour(14).withMinute(50));
        addComment(inc2, demo, "DBサーバーにSSHログイン。PostgreSQLプロセスは起動中だが、接続数が上限に達していることを確認。\nmax_connections=100に対し、102接続が確立されていた。\nスロークエリログを確認中。大量のFull Table Scanが発生していることを確認。",
                LocalDateTime.now().minusDays(57).withHour(16).withMinute(30));

        // === インシデント 3: MEDIUM / RESOLVED ===
        List<String> extracted3 = logAnalysisService.extractErrorLines(rawLog3);
        Incident inc3 = createIncident(demo, log3, "認証サービス SSL証明書期限切れによる認証不可",
                IncidentStatus.RESOLVED, Severity.MEDIUM,
                aiAnalysisService.generateSummary(extracted3),
                aiAnalysisService.generateCause(extracted3),
                aiAnalysisService.generateAction(extracted3),
                LocalDateTime.now().minusDays(32));
        inc3.setUpdatedAt(LocalDateTime.now().minusDays(30));
        incidentRepository.save(inc3);

        addComment(inc3, demo, "認証サービスのエラーログを確認。SSL証明書の有効期限が当日に切れていることを発見。\n証明書: auth.example.com, 有効期限: 2026-04-28 11:59:59",
                LocalDateTime.now().minusDays(32).withHour(12).withMinute(15));
        addComment(inc3, demo, "証明書更新作業を開始。認証局への新規証明書申請を完了。\n新証明書の有効期限: 2027-04-28。インポートおよびサービス再起動を実施。",
                LocalDateTime.now().minusDays(31).withHour(9).withMinute(0));
        addComment(inc3, demo, "認証サービスの正常動作を確認。エラー率がゼロに戻ったことを監視ツールで確認。\n影響ユーザー数: 約230名（12:00〜翌日09:00の約21時間）。事後報告書を作成してクローズ。",
                LocalDateTime.now().minusDays(30).withHour(10).withMinute(30));

        // === インシデント 4: LOW / ON_HOLD ===
        List<String> extracted4 = logAnalysisService.extractErrorLines(rawLog4);
        Incident inc4 = createIncident(demo, log4, "夜間バッチ処理タイムアウト（スロークエリ起因）",
                IncidentStatus.ON_HOLD, Severity.LOW,
                aiAnalysisService.generateSummary(extracted4),
                aiAnalysisService.generateCause(extracted4),
                aiAnalysisService.generateAction(extracted4),
                LocalDateTime.now().minusDays(10));
        addComment(inc4, demo, "夜間バッチの実行時間が通常の2.5倍に増加。スロークエリが原因と推定。\n対象クエリのインデックス最適化が必要だが、本番DBへの影響を考慮し、来月のメンテナンス窓口での対応を検討中。一旦保留とする。",
                LocalDateTime.now().minusDays(9).withHour(9).withMinute(0));

        // === 報告書ドラフト（インシデント3: 対応済み） ===
        ReportDraft draft = reportService.generateAndSave(inc3);
        draft.setCreatedAt(LocalDateTime.now().minusDays(30).withHour(11).withMinute(0));
        reportDraftRepository.save(draft);

        log.info("サンプルデータの初期化が完了しました。インシデント数: 4, ログエントリ数: 4");
    }

    private LogEntry createLogEntry(User user, String title, String rawLog, LocalDateTime createdAt) {
        LogEntry entry = new LogEntry();
        entry.setUser(user);
        entry.setTitle(title);
        entry.setRawLog(rawLog);
        List<String> extracted = logAnalysisService.extractErrorLines(rawLog);
        entry.setExtractedLog(String.join("\n", extracted));
        entry.setCreatedAt(createdAt);
        return logEntryRepository.save(entry);
    }

    private Incident createIncident(User user, LogEntry logEntry, String title,
                                     IncidentStatus status, Severity severity,
                                     String summary, String cause, String action,
                                     LocalDateTime createdAt) {
        Incident incident = new Incident();
        incident.setUser(user);
        incident.setLogEntry(logEntry);
        incident.setTitle(title);
        incident.setStatus(status);
        incident.setSeverity(severity);
        incident.setAiSummary(summary);
        incident.setAiCause(cause);
        incident.setAiAction(action);
        incident.setCreatedAt(createdAt);
        incident.setUpdatedAt(createdAt);
        return incidentRepository.save(incident);
    }

    private void addComment(Incident incident, User user, String text, LocalDateTime createdAt) {
        IncidentComment comment = new IncidentComment();
        comment.setIncident(incident);
        comment.setUser(user);
        comment.setComment(text);
        comment.setCreatedAt(createdAt);
        commentRepository.save(comment);
    }
}
