# OpsLog AI — ログ解析・障害対応管理 Web アプリ

Java / Spring Boot で実装したポートフォリオ用 Web アプリケーションです。  
システムログを貼り付けるだけでエラー行を自動抽出し、AI（テンプレートベース）による解析結果を表示します。障害対応をインシデントとして記録・追跡し、対応完了後の事後報告書ドラフトをワンクリックで生成できます。

---

## 機能一覧

| 機能 | 概要 |
|------|------|
| **ログ解析** | 生ログを貼り付け → ERROR / WARN / Exception などのキーワードでフィルタリング → AI サマリー・原因候補・初動対応案を生成 |
| **インシデント管理** | 重要度（CRITICAL / HIGH / MEDIUM / LOW）とステータス（OPEN / 調査中 / 解決済 / 保留）で障害票を管理。コメントで対応履歴を記録 |
| **事後報告書生成** | 対応済みインシデントから報告書ドラフトを自動生成 |
| **ダッシュボード** | インシデントの件数・重要度の内訳をサマリー表示 |
| **認証** | Spring Security によるログイン認証（BCrypt ハッシュ） |

---

## 技術スタック

| レイヤー | 技術 |
|---------|------|
| 言語 / フレームワーク | Java 17 / Spring Boot 3.3.0 |
| セキュリティ | Spring Security 6（BCrypt、カスタム UserDetailsService） |
| データ永続化 | Spring Data JPA + SQLite（hibernate-community-dialects） |
| フロントエンド | Thymeleaf + Bootstrap 5.3 |
| ビルド | Maven 3.9.6 |
| ユーティリティ | Lombok、Spring Boot DevTools |

---

## ディレクトリ構成

```
src/main/java/com/example/opslogai/
├── config/
│   ├── SecurityConfig.java       # Spring Security 設定
│   └── DataInitializer.java      # 起動時サンプルデータ投入
├── controller/
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── LogAnalysisController.java
│   ├── IncidentController.java
│   └── ReportController.java
├── service/
│   ├── LogAnalysisService.java   # キーワード抽出ロジック
│   ├── AiAnalysisService.java    # AI解析（テンプレートベース実装）
│   ├── IncidentService.java
│   └── ReportService.java
├── entity/                       # JPA エンティティ
├── repository/                   # Spring Data リポジトリ
└── dto/                          # リクエスト / レスポンス DTO
```

---

## データベース設計

| テーブル | 内容 |
|---------|------|
| `users` | ログインユーザー |
| `log_entries` | 投入された生ログと抽出済みエラー行 |
| `incidents` | インシデント票（重要度・ステータス・AI 解析結果） |
| `incident_comments` | インシデントへのコメント（対応履歴） |
| `report_drafts` | 事後報告書ドラフト |

DBファイル（`opslogai.db`）は起動時に自動生成され、サンプルデータ 4件が自動投入されます。

---

## AI 解析について

`AiAnalysisService` がテンプレートベースでサマリー・原因候補・初動対応案を生成します。  
抽出行に含まれるキーワード（`OutOfMemory` / `timeout` / `Connection refused` / `ORA-` など）を判定し、対応する定型文を組み立てる実装です。  
外部 API（OpenAI / Claude）と連携する際は、このクラスを差し替えるだけで対応できる設計になっています。

---

## セットアップ・起動

### 前提条件

- JDK 17
- Maven 3.x

### 起動（Windows）

```cmd
run.cmd
```

`run.cmd` は `JAVA_HOME` と `MAVEN_OPTS`（Windows 証明書ストア対応）を設定した上で `mvn spring-boot:run` を実行します。

### パッケージ化

```cmd
build.cmd
```

`target/opslogai-0.0.1-SNAPSHOT.jar` が生成されます。

### アクセス

| 項目 | 値 |
|------|----|
| URL | http://localhost:18080 |
| デモアカウント | `demo` / `demo123` |

---

## サンプルデータ

起動時に以下の 4 件のインシデントが自動投入されます。

| タイトル | 重要度 | ステータス |
|---------|--------|-----------|
| 本番APサーバー OOMによるサービス停止 | CRITICAL | OPEN |
| DBサーバー 接続タイムアウト多発・サービス影響 | HIGH | 調査中 |
| 認証サービス SSL証明書期限切れによる認証不可 | MEDIUM | 解決済 |
| 夜間バッチ処理タイムアウト（スロークエリ起因） | LOW | 保留 |
