/* OpsLog AI - i18n (client-side translation) */
(function () {
    'use strict';

    var LANG_KEY = 'opslog_lang';

    var translations = {
        ja: {
            /* Navigation */
            'nav.brandSub':           'ログ解析・障害対応管理',
            'nav.main':               'メインメニュー',
            'nav.dashboard':          'ダッシュボード',
            'nav.logAnalysis':        'ログ解析',
            'nav.logInput':           'ログ解析（新規）',
            'nav.logList':            'ログ履歴',
            'nav.incidentMgmt':       '障害管理',
            'nav.incidentList':       '障害一覧',
            'nav.statusOpen':         '未対応',
            'nav.statusInvestigating':'調査中',
            'nav.statusResolved':     '対応済み',
            'nav.aiFunctions':        'AI機能',
            'nav.chat':               'AIチャット検索',
            'nav.settings':           '設定',
            'nav.watch':              '監視設定',
            'nav.logout':             'ログアウト',

            /* Theme */
            'theme.dark':             'ダーク',
            'theme.light':            'ライト',

            /* Login */
            'login.subtitle':         'ログ解析・障害対応管理システム',
            'login.demo':             'デモ環境',
            'login.username':         'ユーザー名',
            'login.password':         'パスワード',
            'login.submit':           'ログイン',
            'login.notice':           '個人情報・機密情報・本番ログは入力しないでください。',
            'login.demoReset':        'デモ環境のため、登録データはリセットされる場合があります。',

            /* Dashboard */
            'dash.title':             'ダッシュボード',
            'dash.totalIncidents':    '総障害件数',
            'dash.open':              '未対応',
            'dash.investigating':     '調査中',
            'dash.resolved':          '対応済み',
            'dash.onHold':            '保留',
            'dash.newAnalysis':       'ログ解析（新規）',
            'dash.recentIncidents':   '最近の障害チケット',
            'dash.recentLogs':        '最近のログ解析',
            'dash.quickActions':      'クイックアクション',
            'dash.startAnalysis':     'ログ解析を開始',
            'dash.createTicket':      '障害チケットを作成',
            'dash.viewIncidents':     '障害一覧を確認',
            'dash.viewAll':           'すべて表示',
            'dash.colTitle':          'タイトル',
            'dash.colSeverity':       '重要度',
            'dash.colStatus':         'ステータス',
            'dash.colCreatedAt':      '作成日',
            'dash.noIncidents':       '障害チケットはありません',
            'dash.noLogs':            'ログエントリはありません',

            /* Log Analysis */
            'log.title':              'ログ解析',
            'log.newAnalysis':        '新規解析',
            'log.notice':             '個人情報・機密情報・本番ログは入力しないでください。デモ用のサンプルログを使用してください。',
            'log.cardTitle':          'ログ貼り付け・解析',
            'log.labelTitle':         '解析タイトル',
            'log.labelPaste':         'ログ貼り付け',
            'log.loadSample':         'サンプルログを読み込む',
            'log.run':                '解析実行',
            'log.history':            '解析履歴',
            'log.flowTitle':          '解析フロー',
            'log.step1':              '① ログ入力',
            'log.step2':              '② エラー抽出',
            'log.step3':              '③ AI解析',
            'log.step4':              '④ チケット作成',
            'log.step5':              '⑤ 報告書生成',

            /* Log List */
            'loglist.title':          'ログ解析履歴',
            'loglist.cardTitle':      '解析済みログ一覧',
            'loglist.colIndex':       '#',
            'loglist.colTitle':       '解析タイトル',
            'loglist.colCategory':    '区分',
            'loglist.colDate':        '解析日時',
            'loglist.colLines':       '抽出行数',
            'loglist.colActions':     '操作',
            'loglist.autoImport':     '自動取込',
            'loglist.manual':         '手動',
            'loglist.viewResult':     '結果表示',
            'loglist.createTicket':   'チケット作成',
            'loglist.empty':          '解析済みログはありません。',
            'loglist.startLink':      'ログ解析を開始する',

            /* Log Result */
            'logresult.title':        '解析結果',
            'logresult.newAnalysis':  '新規解析',
            'logresult.historyBtn':   '履歴',
            'logresult.totalLines':   '総行数',
            'logresult.extractedLines': '抽出行数',
            'logresult.errorRate':    'エラー率',
            'logresult.extractedHeader': '抽出されたエラー行',
            'logresult.noErrors':     'エラー行は検出されませんでした',
            'logresult.createTicket': '障害チケットを作成',
            'logresult.analyzeAnother': '別のログを解析',
            'logresult.analysisHistory': '解析履歴',
            'logresult.aiSummary':    'AI解析サマリー',
            'logresult.rawLog':       '元ログ（プレビュー）',
            'logresult.autoImport':   '自動取込',
            'logresult.sourceFile':   'ソースファイル:',
            'logresult.analysisDate': '解析日時:',

            /* Incident List */
            'inclist.title':          '障害チケット一覧',
            'inclist.newCreate':      '新規作成',
            'inclist.logAnalysis':    'ログ解析',
            'inclist.filterLabel':    'フィルター:',
            'inclist.filterAll':      'すべて',
            'inclist.cardTitle':      'チケット一覧',
            'inclist.filterBy':       'でフィルター中',
            'inclist.colTitle':       'タイトル',
            'inclist.colSeverity':    '重要度',
            'inclist.colStatus':      'ステータス',
            'inclist.colCreatedAt':   '作成日',
            'inclist.colUpdatedAt':   '最終更新',
            'inclist.empty':          '障害チケットはありません。',

            /* Incident Create */
            'create.title':           '障害チケット作成',
            'create.back':            '一覧に戻る',
            'create.relatedLog':      '関連ログエントリ',
            'create.viewResult':      '解析結果を確認',
            'create.basicInfo':       '基本情報',
            'create.incTitle':        '障害タイトル',
            'create.severity':        '重要度',
            'create.initialStatus':   '初期ステータス',
            'create.aiResults':       '解析結果（編集可）',
            'create.autoFilled':      'ログ解析から自動入力されました。内容を確認・編集してください。',
            'create.summary':         '障害概要',
            'create.cause':           '原因候補',
            'create.action':          '初動対応案',
            'create.submit':          'チケットを作成',
            'create.cancel':          'キャンセル',

            /* Incident Detail */
            'inc.info':               '障害情報',
            'inc.title':              '障害タイトル',
            'inc.createdAt':          '発生日時',
            'inc.updatedAt':          '最終更新',
            'inc.relatedLog':         '関連ログ',
            'inc.aiAnalysis':         '解析内容',
            'inc.summary':            '障害概要',
            'inc.cause':              '原因候補',
            'inc.action':             '初動対応案',
            'inc.history':            '対応履歴',
            'inc.noMemo':             '対応メモはまだありません',
            'inc.addMemo':            '対応メモを追加',
            'inc.addBtn':             '追加',
            'inc.statusChange':       'ステータス変更',
            'inc.updateStatus':       'ステータスを更新',
            'inc.toInvestigating':    '調査中に変更',
            'inc.toResolved':         '対応済みに変更',
            'inc.toOnHold':           '保留に変更',
            'inc.report':             '障害報告書',
            'inc.generateReport':     '報告書を生成',
            'inc.viewReport':         '生成済み報告書を確認',
            'inc.relatedLogPreview':  '関連ログ（抜粋）',
            'inc.viewFullLog':        '全文を確認',
            'inc.noExtractedLog':     '抽出ログなし',
            'inc.backToList':         '一覧',
            'inc.reportDesc':         '障害情報・対応履歴を元に報告書ドラフトを生成します。',

            /* Incident Report */
            'report.title':           '障害報告書',
            'report.backBtn':         '障害詳細に戻る',
            'report.ticket':          '障害チケット',
            'report.noDraft':         '報告書がまだ生成されていません。',
            'report.generateBtn':     '報告書を生成する',
            'report.aiDraft':         '障害報告書ドラフト',
            'report.generated':       '生成:',
            'report.copyBtn':         'コピー',
            'report.regenBtn':        '再生成',
            'report.draftWarning':    '本報告書はシステムが自動生成したドラフトです。内容を確認・修正の上、正式報告書として使用してください。',
            'report.operations':      '操作',
            'report.copyText':        'テキストをコピー',
            'report.printBtn':        '印刷',
            'report.backDetail':      '障害詳細に戻る',
            'report.aiInfo':          'AI連携について',

            /* Chat */
            'chat.title':             'AIチャット検索',
            'chat.botName':           'OpsLog AI サポート',
            'chat.online':            'オンライン・DB 検索モード',
            'chat.notice':            'デモ用チャット検索です。個人情報・機密情報・本番ログは入力しないでください。',
            'chat.suggestions':       'よく使う質問',
            'chat.placeholder':       '質問を入力してください… (Enter で送信)',
            'chat.detailPlaceholder': '検索結果の「詳細を見る」をクリックするとここに障害・ログの詳細が表示されます。',
            'chat.historyNote':       '左のチャット履歴はそのまま維持されます。',

            /* Watch Settings */
            'watch.pageTitle':        'ディレクトリ監視設定',
            'watch.currentStatus':    '現在の監視状態',
            'watch.editSettings':     '監視設定を変更',
            'watch.enableLabel':      'ディレクトリ監視を有効にする',
            'watch.directoryLabel':   '監視ディレクトリ',
            'watch.extensionsLabel':  '対象拡張子',
            'watch.saveBtn':          '保存して反映',
            'watch.resetBtn':         'リセット',
            'watch.localNote':        'この機能はローカル実行環境向けです。',
            'watch.running':          '稼働中',
            'watch.stopped':          '停止中',
            'watch.enabledVal':       '有効',
            'watch.disabledVal':      '無効',
            'watch.howTo':            '使い方',

            /* Breadcrumbs */
            'bc.dashboard':           'ダッシュボード',
            'bc.logHistory':          'ログ履歴',
            'bc.logAnalysis':         'ログ解析',
            'bc.logResult':           '解析結果',
            'bc.incidents':           '障害一覧',
            'bc.newCreate':           '新規作成',
            'bc.report':              '報告書',
            'bc.watchSettings':       '監視設定',

            /* Common */
            'common.demoNotice':      'デモ環境のため、登録データはリセットされる場合があります。',
            'common.securityNotice':  '個人情報・機密情報・本番ログは入力しないでください。',
            'common.notAvailable':    '（記載なし）'
        },

        en: {
            /* Navigation */
            'nav.brandSub':           'Log Analysis & Incident Mgmt',
            'nav.main':               'MAIN MENU',
            'nav.dashboard':          'Dashboard',
            'nav.logAnalysis':        'LOG ANALYSIS',
            'nav.logInput':           'New Analysis',
            'nav.logList':            'Log History',
            'nav.incidentMgmt':       'INCIDENTS',
            'nav.incidentList':       'Incidents',
            'nav.statusOpen':         'Open',
            'nav.statusInvestigating':'Investigating',
            'nav.statusResolved':     'Resolved',
            'nav.aiFunctions':        'AI FEATURES',
            'nav.chat':               'AI Chat Search',
            'nav.settings':           'SETTINGS',
            'nav.watch':              'Watch Settings',
            'nav.logout':             'Logout',

            /* Theme */
            'theme.dark':             'Dark',
            'theme.light':            'Light',

            /* Login */
            'login.subtitle':         'Log Analysis & Incident Management',
            'login.demo':             'Demo Environment',
            'login.username':         'Username',
            'login.password':         'Password',
            'login.submit':           'Login',
            'login.notice':           'Do not enter personal, confidential, or production log data.',
            'login.demoReset':        'Demo environment — registered data may be reset.',

            /* Dashboard */
            'dash.title':             'Dashboard',
            'dash.totalIncidents':    'Total Incidents',
            'dash.open':              'Open',
            'dash.investigating':     'Investigating',
            'dash.resolved':          'Resolved',
            'dash.onHold':            'On Hold',
            'dash.newAnalysis':       'New Log Analysis',
            'dash.recentIncidents':   'Recent Incident Tickets',
            'dash.recentLogs':        'Recent Log Analysis',
            'dash.quickActions':      'Quick Actions',
            'dash.startAnalysis':     'Start Log Analysis',
            'dash.createTicket':      'Create Incident Ticket',
            'dash.viewIncidents':     'View Incidents',
            'dash.viewAll':           'View All',
            'dash.colTitle':          'Title',
            'dash.colSeverity':       'Severity',
            'dash.colStatus':         'Status',
            'dash.colCreatedAt':      'Created',
            'dash.noIncidents':       'No incident tickets',
            'dash.noLogs':            'No log entries',

            /* Log Analysis */
            'log.title':              'Log Analysis',
            'log.newAnalysis':        'New Analysis',
            'log.notice':             'Do not enter personal, confidential, or production log data. Use sample logs for demo.',
            'log.cardTitle':          'Paste & Analyze Log',
            'log.labelTitle':         'Analysis Title',
            'log.labelPaste':         'Paste Log',
            'log.loadSample':         'Load Sample Log',
            'log.run':                'Run Analysis',
            'log.history':            'Analysis History',
            'log.flowTitle':          'Analysis Flow',
            'log.step1':              '① Input Log',
            'log.step2':              '② Extract Errors',
            'log.step3':              '③ AI Analysis',
            'log.step4':              '④ Create Ticket',
            'log.step5':              '⑤ Generate Report',

            /* Log List */
            'loglist.title':          'Log Analysis History',
            'loglist.cardTitle':      'Analyzed Logs',
            'loglist.colIndex':       '#',
            'loglist.colTitle':       'Title',
            'loglist.colCategory':    'Type',
            'loglist.colDate':        'Date',
            'loglist.colLines':       'Lines',
            'loglist.colActions':     'Actions',
            'loglist.autoImport':     'Auto Import',
            'loglist.manual':         'Manual',
            'loglist.viewResult':     'View Result',
            'loglist.createTicket':   'Create Ticket',
            'loglist.empty':          'No analyzed logs found.',
            'loglist.startLink':      'Start log analysis',

            /* Log Result */
            'logresult.title':        'Analysis Result',
            'logresult.newAnalysis':  'New Analysis',
            'logresult.historyBtn':   'History',
            'logresult.totalLines':   'Total Lines',
            'logresult.extractedLines': 'Extracted Lines',
            'logresult.errorRate':    'Error Rate',
            'logresult.extractedHeader': 'Extracted Error Lines',
            'logresult.noErrors':     'No error lines detected',
            'logresult.createTicket': 'Create Incident Ticket',
            'logresult.analyzeAnother': 'Analyze Another Log',
            'logresult.analysisHistory': 'Analysis History',
            'logresult.aiSummary':    'AI Analysis Summary',
            'logresult.rawLog':       'Raw Log (Preview)',
            'logresult.autoImport':   'Auto Import',
            'logresult.sourceFile':   'Source File:',
            'logresult.analysisDate': 'Analysis Date:',

            /* Incident List */
            'inclist.title':          'Incident Tickets',
            'inclist.newCreate':      'New',
            'inclist.logAnalysis':    'Log Analysis',
            'inclist.filterLabel':    'Filter:',
            'inclist.filterAll':      'All',
            'inclist.cardTitle':      'Ticket List',
            'inclist.filterBy':       'filtered',
            'inclist.colTitle':       'Title',
            'inclist.colSeverity':    'Severity',
            'inclist.colStatus':      'Status',
            'inclist.colCreatedAt':   'Created',
            'inclist.colUpdatedAt':   'Updated',
            'inclist.empty':          'No incident tickets found.',

            /* Incident Create */
            'create.title':           'Create Incident Ticket',
            'create.back':            'Back to List',
            'create.relatedLog':      'Related Log Entry',
            'create.viewResult':      'View Analysis Result',
            'create.basicInfo':       'Basic Info',
            'create.incTitle':        'Incident Title',
            'create.severity':        'Severity',
            'create.initialStatus':   'Initial Status',
            'create.aiResults':       'AI Analysis (Editable)',
            'create.autoFilled':      'Auto-filled from log analysis. Please review and edit.',
            'create.summary':         'Summary',
            'create.cause':           'Possible Cause',
            'create.action':          'Initial Response',
            'create.submit':          'Create Ticket',
            'create.cancel':          'Cancel',

            /* Incident Detail */
            'inc.info':               'Incident Info',
            'inc.title':              'Incident Title',
            'inc.createdAt':          'Occurred',
            'inc.updatedAt':          'Last Updated',
            'inc.relatedLog':         'Related Log',
            'inc.aiAnalysis':         'AI Analysis',
            'inc.summary':            'Summary',
            'inc.cause':              'Possible Cause',
            'inc.action':             'Initial Response',
            'inc.history':            'Response History',
            'inc.noMemo':             'No response memos yet',
            'inc.addMemo':            'Add Response Memo',
            'inc.addBtn':             'Add',
            'inc.statusChange':       'Change Status',
            'inc.updateStatus':       'Update Status',
            'inc.toInvestigating':    'Set to Investigating',
            'inc.toResolved':         'Set to Resolved',
            'inc.toOnHold':           'Set to On Hold',
            'inc.report':             'Incident Report',
            'inc.generateReport':     'Generate Report',
            'inc.viewReport':         'View Generated Report',
            'inc.relatedLogPreview':  'Related Log (Preview)',
            'inc.viewFullLog':        'View Full Log',
            'inc.noExtractedLog':     'No extracted log',
            'inc.backToList':         'List',
            'inc.reportDesc':         'Generates a report draft from incident info and response history.',

            /* Incident Report */
            'report.title':           'Incident Report',
            'report.backBtn':         'Back to Incident',
            'report.ticket':          'Incident Ticket',
            'report.noDraft':         'No report has been generated yet.',
            'report.generateBtn':     'Generate Report',
            'report.aiDraft':         'Incident Report Draft',
            'report.generated':       'Generated:',
            'report.copyBtn':         'Copy',
            'report.regenBtn':        'Regenerate',
            'report.draftWarning':    'This report was auto-generated. Please review and edit before using as an official report.',
            'report.operations':      'Actions',
            'report.copyText':        'Copy Text',
            'report.printBtn':        'Print',
            'report.backDetail':      'Back to Incident',
            'report.aiInfo':          'About AI Integration',

            /* Chat */
            'chat.title':             'AI Chat Search',
            'chat.botName':           'OpsLog AI Support',
            'chat.online':            'Online · DB Search Mode',
            'chat.notice':            'Demo chat search. Do not enter personal, confidential, or production log data.',
            'chat.suggestions':       'Common Queries',
            'chat.placeholder':       'Type your question… (Enter to send)',
            'chat.detailPlaceholder': 'Click "View Details" on a search result to display incident or log details here.',
            'chat.historyNote':       'Chat history on the left will be maintained.',

            /* Watch Settings */
            'watch.pageTitle':        'Watch Settings',
            'watch.currentStatus':    'Current Status',
            'watch.editSettings':     'Edit Settings',
            'watch.enableLabel':      'Enable Directory Watch',
            'watch.directoryLabel':   'Watch Directory',
            'watch.extensionsLabel':  'File Extensions',
            'watch.saveBtn':          'Save & Apply',
            'watch.resetBtn':         'Reset',
            'watch.localNote':        'This feature is for local environments only.',
            'watch.running':          'Running',
            'watch.stopped':          'Stopped',
            'watch.enabledVal':       'Enabled',
            'watch.disabledVal':      'Disabled',
            'watch.howTo':            'How to Use',

            /* Breadcrumbs */
            'bc.dashboard':           'Dashboard',
            'bc.logHistory':          'Log History',
            'bc.logAnalysis':         'Log Analysis',
            'bc.logResult':           'Analysis Result',
            'bc.incidents':           'Incidents',
            'bc.newCreate':           'New',
            'bc.report':              'Report',
            'bc.watchSettings':       'Watch Settings',

            /* Common */
            'common.demoNotice':      'Demo environment — registered data may be reset.',
            'common.securityNotice':  'Do not enter personal, confidential, or production log data.',
            'common.notAvailable':    '(Not available)'
        }
    };

    function getLang() {
        return localStorage.getItem(LANG_KEY) || 'ja';
    }

    function applyElement(el) {
        var key  = el.getAttribute('data-i18n');
        if (!key) return;
        var dict = translations[getLang()] || translations.ja;
        if (dict[key] !== undefined) {
            el.textContent = dict[key];
        }
    }

    function applyAll() {
        var lang = getLang();
        var dict = translations[lang] || translations.ja;

        document.querySelectorAll('[data-i18n]').forEach(function (el) {
            var key = el.getAttribute('data-i18n');
            if (dict[key] !== undefined) {
                el.textContent = dict[key];
            }
        });

        document.querySelectorAll('[data-i18n-placeholder]').forEach(function (el) {
            var key = el.getAttribute('data-i18n-placeholder');
            if (dict[key] !== undefined) {
                el.setAttribute('placeholder', dict[key]);
            }
        });

        var langLabel = document.getElementById('lang-label');
        if (langLabel) {
            langLabel.textContent = lang === 'ja' ? 'EN' : 'JA';
        }

        document.documentElement.lang = lang === 'ja' ? 'ja' : 'en';
    }

    /* Expose early so theme.js DOMContentLoaded handler can call applyElement */
    window.OpsI18n = {
        apply: applyAll,
        applyElement: applyElement,
        getLang: getLang
    };

    document.addEventListener('DOMContentLoaded', function () {
        applyAll();

        var btn = document.getElementById('lang-toggle');
        if (btn) {
            btn.addEventListener('click', function () {
                var next = getLang() === 'ja' ? 'en' : 'ja';
                localStorage.setItem(LANG_KEY, next);
                applyAll();
                /* Re-sync theme toggle label */
                if (window.OpsTheme) {
                    var theme = document.documentElement.getAttribute('data-theme') || 'light';
                    window.OpsTheme.updateUI(theme);
                }
            });
        }
    });
})();
