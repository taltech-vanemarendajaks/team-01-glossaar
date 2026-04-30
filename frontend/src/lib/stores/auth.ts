import { writable, derived } from 'svelte/store';
import { GlossarClient, type MeResponse } from '$lib/api/glossarClient';
import { translateError } from '$lib/i18n/translateError';

type AuthState = {
    user: MeResponse | null;
    loading: boolean;
    error: string | null;
};

function createAuthStore() {
    const { subscribe, update } = writable<AuthState>({
        user: null,
        loading: true,
        error: null
    });

    return {
        subscribe,

        async init() {
            try {
                const user = await GlossarClient.getMe();
                update((state) => ({ ...state, user, loading: false, error: null }));
            } catch (err) {
                const error = translateError(err, 'request: failed');
                update((state) => ({ ...state, user: null, loading: false, error }));
            }
        },

        async logout() {
            try {
                update((state) => ({ ...state, user: null, error: null }));
                // TODO: purge session cookie on backend
            } catch (err) {
                const error = translateError(err, 'request: failed');
                update((state) => ({ ...state, error }));
            }
        }
    };
}

export const auth = createAuthStore();

export const isAuthenticated = derived(auth, ($auth) => !!$auth.user);
export const isLoading = derived(auth, ($auth) => $auth.loading);
export const user = derived(auth, ($auth) => $auth.user);
export const authError = derived(auth, ($auth) => $auth.error);
