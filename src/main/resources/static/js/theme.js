/* OpsLog AI - Theme management */
(function () {
    'use strict';

    var THEME_KEY = 'opslog_theme';

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
    }

    /* Apply immediately (called inline in <head> to prevent FOUC) */
    applyTheme(localStorage.getItem(THEME_KEY) || 'light');

    function updateToggleUI(theme) {
        var icon  = document.getElementById('theme-icon');
        var label = document.getElementById('theme-label');
        if (icon) {
            icon.className = theme === 'dark' ? 'bi bi-sun-fill' : 'bi bi-moon-fill';
        }
        if (label) {
            /* Switch i18n key to show target state */
            label.setAttribute('data-i18n', theme === 'dark' ? 'theme.light' : 'theme.dark');
            if (window.OpsI18n && window.OpsI18n.applyElement) {
                window.OpsI18n.applyElement(label);
            }
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        var current = localStorage.getItem(THEME_KEY) || 'light';
        updateToggleUI(current);

        var btn = document.getElementById('theme-toggle');
        if (btn) {
            btn.addEventListener('click', function () {
                var next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
                localStorage.setItem(THEME_KEY, next);
                applyTheme(next);
                updateToggleUI(next);
            });
        }
    });

    /* Expose for cross-script use */
    window.OpsTheme = {
        apply: applyTheme,
        updateUI: updateToggleUI
    };
})();
