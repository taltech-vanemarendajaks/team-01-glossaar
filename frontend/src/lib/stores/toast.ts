import { writable } from 'svelte/store';
import { _ } from 'svelte-i18n';


export type ToastType = 'success' | 'error';

export type Toast = {
    title: string;
    subtitle: string | null;
    type: ToastType;
};

export const TOAST_ANIMATION_DURATION = 400;

const TOAST_TIMEOUT: Record<ToastType, number> = {
    success: 3000,
    error: 6000,
};

// TODO: consider allowing multiple toasts at once
function createToastStore() {
    const { subscribe, update } = writable<Toast | null>();
    let timeoutId: ReturnType<typeof setTimeout>;

    const clear = () => {
        clearTimeout(timeoutId!);
        update(() => null)
    };

    const add = (title: string, subtitle: string | null = null, type: ToastType = 'success') => {
        clear();

        const timeout = TOAST_TIMEOUT[type] + 2 * TOAST_ANIMATION_DURATION;
        timeoutId = setTimeout(clear, timeout);
        update(() => ({ title, subtitle, type }));
    };

    return {
        subscribe,
        clear,
        success: (title: string, subtitle: string | null = null) => add(title, subtitle, 'success'),
        // TODO: handle destructuring message from API error here instead of doing it every time where an api request is made?
        error: (title: string | null = null, subtitle: string | null = null) => add(title || _('common.error'), subtitle, 'error'),
    };
}

export const toast = createToastStore();