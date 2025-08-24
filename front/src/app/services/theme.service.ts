import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
    private key = 'eb-theme';

    constructor() {
        const saved = localStorage.getItem(this.key);
        // apply both html and body classes so CSS rules using either strategy work
        if (saved === 'dark') {
            document.documentElement.classList.add('dark');
            document.documentElement.classList.remove('light');
            document.body.classList.add('dark-theme');
        } else if (saved === 'light') {
            document.documentElement.classList.add('light');
            document.documentElement.classList.remove('dark');
            document.body.classList.remove('dark-theme');
        } else {
            // default to light
            document.documentElement.classList.add('light');
            document.body.classList.remove('dark-theme');
        }
    }

    toggle() {
        const isDark = document.documentElement.classList.toggle('dark');
        // keep html.light inverse in sync
        if (isDark) document.documentElement.classList.remove('light'); else document.documentElement.classList.add('light');
        document.body.classList.toggle('dark-theme', isDark);
        localStorage.setItem(this.key, isDark ? 'dark' : 'light');
    }

    setDark(dark: boolean) {
        document.documentElement.classList.toggle('dark', dark);
        document.documentElement.classList.toggle('light', !dark);
        document.body.classList.toggle('dark-theme', dark);
        localStorage.setItem(this.key, dark ? 'dark' : 'light');
    }

    isDark() {
        return document.documentElement.classList.contains('dark') || document.body.classList.contains('dark-theme');
    }
}
