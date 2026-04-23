import { browser } from '$app/environment';
import { init, register, locale } from 'svelte-i18n';

export const SUPPORTED_LOCALES = ['et', 'en'] as const;
export type SupportedLocale = (typeof SUPPORTED_LOCALES)[number];

const DEFAULT_LOCALE: SupportedLocale = 'et';
const STORAGE_KEY = 'glossaar.locale';

register('et', () => import('./locales/et.json'));
register('en', () => import('./locales/en.json'));

function detectInitialLocale(): SupportedLocale {
    if (!browser) return DEFAULT_LOCALE;
    const stored = window.localStorage.getItem(STORAGE_KEY);
    if (stored && (SUPPORTED_LOCALES as readonly string[]).includes(stored)) {
        return stored as SupportedLocale;
    }
    return DEFAULT_LOCALE;
}

init({
    fallbackLocale: DEFAULT_LOCALE,
    initialLocale: detectInitialLocale()
});

export function setLocale(next: SupportedLocale) {
    locale.set(next);
    if (browser) {
        window.localStorage.setItem(STORAGE_KEY, next);
    }
}
