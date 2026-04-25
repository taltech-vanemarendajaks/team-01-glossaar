import { writable } from 'svelte/store';
import { _ } from 'svelte-i18n';


export type ToastType = 'success' | 'error';

export type Toast = {
    title: string;
    subtitle: string | null;
    type: ToastType;
    timeout: number;
};

const TOAST_TIMEOUTS: Record<ToastType, number> = {
    success: 3000,
    error: 5000,
};

// TODO: consider allowing multiple toasts at once
function createToastStore() {
    const { subscribe, update } = writable<Toast | null>();

    const clear = () => update(() => null);

    const add = (title: string, subtitle: string | null = null, type: ToastType = 'success') => {
        const timeout = TOAST_TIMEOUTS[type];
        update(() => ({ title, subtitle, type, timeout }));
        setTimeout(clear, timeout);
    };

    return {
        subscribe,
        clear,
        success: (title: string, subtitle: string | null = null) => add(title, subtitle, 'success'),
        // TODO: handle destructuring message from API error here?
        error: (title: string | null = null, subtitle: string | null = null) => add(title || _('common.error'), subtitle, 'error'),
    };
}

export const toast = createToastStore();