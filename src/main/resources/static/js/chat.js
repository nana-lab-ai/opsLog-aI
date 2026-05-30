'use strict';

/* =========================================================
   OpsLog AI — chat.js
   Handles: async search, bubble rendering, detail pane,
            session history (sessionStorage)
   ========================================================= */

const HISTORY_KEY = 'opslog_chat_history_v1';

const WELCOME = {
    message: 'こんにちは。OpsLog AI サポートです。\n障害チケット・ログ・対応履歴・報告書をチャット形式で検索できます。\n\n例：「未対応の障害を教えて」「ORA-12541 の障害はある？」「timeout が出ているログを探して」',
    hasResults: false,
    results: [],
    query: null
};

/* ---------------------------------------------------------
   Init
   --------------------------------------------------------- */
document.addEventListener('DOMContentLoaded', function () {
    renderWelcome();
    restoreHistory();
    scrollBottom();
    bindEvents();
});

function bindEvents() {
    // Send button
    document.getElementById('send-btn').addEventListener('click', handleSend);

    // Enter to send (Shift+Enter = newline)
    document.getElementById('chat-input').addEventListener('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    });

    // Auto-resize textarea
    document.getElementById('chat-input').addEventListener('input', function () {
        this.style.height = 'auto';
        this.style.height = Math.min(this.scrollHeight, 110) + 'px';
    });

    // Suggestion chips
    document.querySelectorAll('.chip').forEach(function (chip) {
        chip.addEventListener('click', function () {
            var input = document.getElementById('chat-input');
            input.value = this.textContent.trim();
            input.dispatchEvent(new Event('input')); // trigger resize
            input.focus();
        });
    });

    // Detail button clicks (event delegation — handles dynamically added buttons)
    document.getElementById('chat-messages').addEventListener('click', function (e) {
        var btn = e.target.closest('.btn-detail');
        if (btn) {
            loadDetail(btn.dataset.detailUrl, btn.dataset.fullUrl);
        }
    });
}

/* ---------------------------------------------------------
   Send handling
   --------------------------------------------------------- */
function handleSend() {
    var input = document.getElementById('chat-input');
    var query = input.value.trim();
    if (!query) return;

    input.value = '';
    input.style.height = 'auto';
    setSendEnabled(false);

    var time = nowTime();
    renderUserBubble(query, time);
    appendToHistory({ role: 'user', text: query, time: time });

    var typingId = showTyping();

    fetch('/chat/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [window.CSRF_HEADER]: window.CSRF_TOKEN
        },
        body: JSON.stringify({ query: query })
    })
        .then(function (res) {
            if (res.status === 429) {
                return res.json().then(function (d) {
                    throw { rateLimited: true, message: d.message || '短時間に連続操作が行われました。少し待ってから再実行してください。' };
                });
            }
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        })
        .then(function (data) {
            hideTyping(typingId);
            var aiTime = nowTime();
            renderAIBubble(data, aiTime, false);
            appendToHistory({ role: 'ai', data: data, time: aiTime });
        })
        .catch(function (err) {
            hideTyping(typingId);
            var msg = (err && err.rateLimited)
                ? err.message
                : '検索中にエラーが発生しました。もう一度お試しください。';
            var errData = {
                message: msg,
                hasResults: false,
                results: []
            };
            renderAIBubble(errData, nowTime(), false);
        })
        .finally(function () {
            setSendEnabled(true);
            document.getElementById('chat-input').focus();
        });
}

/* ---------------------------------------------------------
   Render helpers
   --------------------------------------------------------- */
function renderWelcome() {
    var el = buildAIRow(WELCOME, '', true);
    document.getElementById('chat-messages').appendChild(el);
}

function renderUserBubble(text, time) {
    var row = document.createElement('div');
    row.className = 'msg-row user-row';
    row.innerHTML =
        '<div class="msg-body">' +
        '  <div class="msg-bubble user">' + esc(text) + '</div>' +
        '  <div class="msg-time">' + esc(time) + '</div>' +
        '</div>' +
        '<div class="msg-avatar user"><i class="bi bi-person-fill"></i></div>';
    appendToChat(row);
}

function renderAIBubble(data, time, isWelcome) {
    var el = buildAIRow(data, time, isWelcome);
    appendToChat(el);
}

function buildAIRow(data, time, isWelcome) {
    var row = document.createElement('div');
    row.className = 'msg-row ai-row' + (isWelcome ? ' welcome-row' : '');

    var cardsHtml = '';
    if (data.hasResults && data.results && data.results.length > 0) {
        cardsHtml = '<div class="result-cards">' +
            data.results.map(buildCardHtml).join('') +
            '</div>';
    }

    var timeHtml = isWelcome ? '' : '<div class="msg-time">' + esc(time) + '</div>';

    row.innerHTML =
        '<div class="msg-avatar ai"><i class="bi bi-robot"></i></div>' +
        '<div class="msg-body">' +
        '  <div class="msg-sender">OpsLog AI サポート</div>' +
        '  <div class="msg-bubble ai">' + esc(data.message).replace(/\n/g, '<br>') + '</div>' +
        cardsHtml +
        timeHtml +
        '</div>';

    return row;
}

function buildCardHtml(r) {
    var isIncident = r.type === 'incident';
    var detailUrl = '/chat/detail/' + r.type + '/' + extractId(r.linkUrl);
    var fullUrl   = r.linkUrl || '#';

    var typeCls = isIncident ? 'incident' : 'log';
    var typeLabel = isIncident ? '障害' : 'ログ';

    var badges =
        '<span class="type-tag ' + typeCls + '">' + typeLabel + '</span>' +
        (r.severityDisplay ? ' <span class="status-badge ' + escAttr(r.severityCss || '') + '">' + esc(r.severityDisplay) + '</span>' : '') +
        (r.statusDisplay   ? ' <span class="status-badge ' + escAttr(r.statusCss || '')   + '">' + esc(r.statusDisplay)   + '</span>' : '') +
        (r.matchedField    ? ' <span class="type-tag match">' + esc(r.matchedField) + '</span>' : '');

    var summary = r.summary
        ? '<div class="result-card-summary">' + esc(r.summary) + '</div>'
        : '';

    return '<div class="result-card type-' + typeCls + '">' +
        '<div class="result-card-badges">' + badges + '</div>' +
        '<div class="result-card-title">' + esc(r.title || '') + '</div>' +
        summary +
        '<div class="result-card-foot">' +
        '  <button class="btn-detail"' +
        '          data-detail-url="' + escAttr(detailUrl) + '"' +
        '          data-full-url="' + escAttr(fullUrl) + '">' +
        '    <i class="bi bi-arrow-right-circle"></i> 詳細を見る' +
        '  </button>' +
        '</div>' +
        '</div>';
}

/* ---------------------------------------------------------
   Detail pane
   --------------------------------------------------------- */
function loadDetail(detailUrl, fullUrl) {
    var pane = document.getElementById('detail-pane');
    pane.innerHTML =
        '<div class="detail-loading">' +
        '  <i class="bi bi-arrow-repeat spin"></i>読み込み中...' +
        '</div>';

    fetch(detailUrl, {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    })
        .then(function (res) {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.text();
        })
        .then(function (html) {
            if (!html || html.trim() === '') throw new Error('empty');
            pane.innerHTML = html;
        })
        .catch(function () {
            pane.innerHTML =
                '<div class="detail-placeholder">' +
                '  <i class="bi bi-exclamation-circle ph-icon" style="color:#dc2626;"></i>' +
                '  <p>詳細の読み込みに失敗しました。</p>' +
                '  <a href="' + escAttr(fullUrl) + '" class="detail-full-link" target="_blank">' +
                '    <i class="bi bi-box-arrow-up-right"></i> 別タブで開く' +
                '  </a>' +
                '</div>';
        });
}

/* ---------------------------------------------------------
   Session history (sessionStorage)
   --------------------------------------------------------- */
function restoreHistory() {
    var hist = loadHistory();
    hist.forEach(function (item) {
        if (item.role === 'user') {
            renderUserBubble(item.text, item.time);
        } else if (item.role === 'ai') {
            renderAIBubble(item.data, item.time, false);
        }
    });
}

function loadHistory() {
    try {
        return JSON.parse(sessionStorage.getItem(HISTORY_KEY) || '[]');
    } catch (e) {
        return [];
    }
}

function appendToHistory(item) {
    try {
        var hist = loadHistory();
        hist.push(item);
        // Cap history at 40 items to avoid exceeding storage limits
        if (hist.length > 40) hist = hist.slice(-40);
        sessionStorage.setItem(HISTORY_KEY, JSON.stringify(hist));
    } catch (e) { /* sessionStorage full or unavailable — silently ignore */ }
}

/* ---------------------------------------------------------
   Typing indicator
   --------------------------------------------------------- */
function showTyping() {
    var id = 'typing-' + Date.now();
    var row = document.createElement('div');
    row.id = id;
    row.className = 'msg-row ai-row typing-row';
    row.innerHTML =
        '<div class="msg-avatar ai"><i class="bi bi-robot"></i></div>' +
        '<div class="msg-body">' +
        '  <div class="msg-sender">OpsLog AI サポート</div>' +
        '  <div class="msg-bubble ai">' +
        '    <div class="typing-dots"><span></span><span></span><span></span></div>' +
        '  </div>' +
        '</div>';
    appendToChat(row);
    return id;
}

function hideTyping(id) {
    var el = document.getElementById(id);
    if (el) el.remove();
}

/* ---------------------------------------------------------
   Utilities
   --------------------------------------------------------- */
function appendToChat(el) {
    var container = document.getElementById('chat-messages');
    container.appendChild(el);
    scrollBottom();
}

function scrollBottom() {
    var c = document.getElementById('chat-messages');
    if (c) c.scrollTop = c.scrollHeight;
}

function setSendEnabled(enabled) {
    document.getElementById('send-btn').disabled = !enabled;
}

function nowTime() {
    return new Date().toLocaleTimeString('ja-JP', { hour: '2-digit', minute: '2-digit' });
}

function extractId(url) {
    if (!url) return '0';
    var parts = url.split('/');
    return parts[parts.length - 1] || '0';
}

/* XSS-safe escaping */
function esc(s) {
    if (s == null) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;');
}

function escAttr(s) {
    if (s == null) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;');
}
