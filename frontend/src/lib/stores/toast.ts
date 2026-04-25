import { writable } from 'svelte/store';

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
        error: (title: string, subtitle: string | null = null) => add(title, subtitle, 'error'),
    };
}

export const toast = createToastStore();