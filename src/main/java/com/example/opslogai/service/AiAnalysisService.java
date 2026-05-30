package com.example.opslogai.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI解析サービス。現在はテンプレートベースのダミー実装。
 * OpenAI API / Claude API 連携時はこのクラスを差し替える。
 */
@Service
public class AiAnalysisService {

    public String generateSummary(List<String> extractedLines) {
        if (extractedLines.isEmpty()) {
            return "抽出されたエラー行はありませんでした。";
        }
        long errorCount = extractedLines.stream()
                .filter(l -> l.toUpperCase().contains("ERROR")).count();
        long warnCount = extractedLines.stream()
                .filter(l -> l.toUpperCase().contains("WARN")).count();
        long exceptionCount = extractedLines.stream()
                .filter(l -> l.contains("Exception")).count();
        long timeoutCount = extractedLines.stream()
                .filter(l -> l.toLowerCase().contains("timeout")).count();

        StringBuilder sb = new StringBuilder();
        sb.append("【AI解析サマリー（テンプレート生成）】\n");
        sb.append(String.format("抽出行数: %d行\n", extractedLines.size()));
        if (errorCount > 0) sb.append(String.format("・ERROR: %d件\n", errorCount));
        if (warnCount > 0) sb.append(String.format("・WARN: %d件\n", warnCount));
        if (exceptionCount > 0) sb.append(String.format("・Exception: %d件\n", exceptionCount));
        if (timeoutCount > 0) sb.append(String.format("・タイムアウト系: %d件\n", timeoutCount));
        sb.append("\n最初のエラー行:\n");
        extractedLines.stream().limit(3).forEach(l -> sb.append("  ").append(l.trim()).append("\n"));
        return sb.toString();
    }

    public String generateCause(List<String> extractedLines) {
        boolean hasOom = extractedLines.stream()
                .anyMatch(l -> l.contains("OutOfMemory") || l.contains("heap space"));
        boolean hasTimeout = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("timeout"));
        boolean hasConnection = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("connection refused") || l.toLowerCase().contains("refused"));
        boolean hasOra = extractedLines.stream()
                .anyMatch(l -> l.contains("ORA-"));
        boolean hasAuth = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("denied") || l.toLowerCase().contains("authentication failed"));
        boolean hasDisk = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("disk") || l.toLowerCase().contains("no space"));

        StringBuilder sb = new StringBuilder("【原因候補（テンプレート生成）】\n");
        if (hasOom) sb.append("・Javaヒープメモリ不足 (OutOfMemoryError)\n  → JVM起動オプション -Xmx の見直し、メモリリークの調査が必要\n");
        if (hasTimeout) sb.append("・接続/処理タイムアウト\n  → ネットワーク遅延、DBサーバー負荷、コネクションプール枯渇が疑われる\n");
        if (hasConnection) sb.append("・サービス接続拒否\n  → 対象サービスのダウン、ファイアウォール設定、ポート疎通を確認\n");
        if (hasOra) sb.append("・Oracleデータベースエラー\n  → ORAエラーコードを参照し、SQLまたはDB設定を確認\n");
        if (hasAuth) sb.append("・認証/権限エラー\n  → 証明書の有効期限、パスワード変更、ACL設定を確認\n");
        if (hasDisk) sb.append("・ディスク容量不足\n  → ログローテーション設定、不要ファイルの削除を実施\n");
        if (sb.toString().equals("【原因候補（テンプレート生成）】\n")) {
            sb.append("・詳細調査が必要です。ログの前後の文脈を確認してください。\n");
        }
        return sb.toString();
    }

    public String generateAction(List<String> extractedLines) {
        boolean hasOom = extractedLines.stream()
                .anyMatch(l -> l.contains("OutOfMemory") || l.contains("heap space"));
        boolean hasTimeout = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("timeout"));
        boolean hasConnection = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("connection refused") || l.toLowerCase().contains("refused"));
        boolean hasAuth = extractedLines.stream()
                .anyMatch(l -> l.toLowerCase().contains("denied") || l.toLowerCase().contains("authentication"));

        StringBuilder sb = new StringBuilder("【初動対応案（テンプレート生成）】\n");
        sb.append("■ 共通確認事項\n");
        sb.append("  1. 現在のサービス稼働状況を確認（監視ツール・死活監視）\n");
        sb.append("  2. 影響範囲の特定（ユーザー数、機能範囲）\n");
        sb.append("  3. 発生時刻とログの時系列を整理\n\n");
        if (hasOom) {
            sb.append("■ メモリ不足対応\n");
            sb.append("  1. ヒープダンプを取得（-XX:+HeapDumpOnOutOfMemoryError）\n");
            sb.append("  2. アプリケーション再起動（応急処置）\n");
            sb.append("  3. GCログを確認し、メモリ使用量のトレンドを分析\n\n");
        }
        if (hasTimeout) {
            sb.append("■ タイムアウト対応\n");
            sb.append("  1. 対象サーバーの負荷状況確認（CPU/メモリ/接続数）\n");
            sb.append("  2. コネクションプールの設定値を確認\n");
            sb.append("  3. スロークエリログを確認\n\n");
        }
        if (hasConnection) {
            sb.append("■ 接続エラー対応\n");
            sb.append("  1. 対象サービスのプロセス状態を確認\n");
            sb.append("  2. ネットワーク疎通確認（ping / telnet）\n");
            sb.append("  3. ファイアウォール・セキュリティグループの設定確認\n\n");
        }
        if (hasAuth) {
            sb.append("■ 認証エラー対応\n");
            sb.append("  1. 証明書・トークンの有効期限を確認\n");
            sb.append("  2. 認証サービスの状態確認\n");
            sb.append("  3. アクセス権限・ロール設定を確認\n\n");
        }
        return sb.toString();
    }
}
