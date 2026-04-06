import { writable, derived } from 'svelte/store';
import { GlossarClient, type MeResponse } from '$lib/api/glossarClient';

function createAuthStore() {
    const { subscribe, set } = writable<{
        data: MeResponse | null;
        loading: boolean;
        isAuthenticated: boolean;
    }>({
        data: null,
        loading: true,
        isAuthenticated: false
    });

    async function init() {
        try {
            const data = await GlossarClient.getMe();
            set({ data, loading: false, isAuthenticated: true });
        } catch {
            set({ data: null, loading: false, isAuthenticated: false });
        }
    }

    function logout() {
        set({ data: null, loading: false, isAuthenticated: false });
        // TODO: invalidate cookies and log out on server side as well
    }

    return {
        subscribe,
        set,
        init,
        logout
    };
}

export const auth = createAuthStore();

export const isAuthenticated = derived(auth, ($auth) => $auth.isAuthenticated);
export const isLoading = derived(auth, ($auth) => $auth.loading);
export const authData = derived(auth, ($auth) => $auth.data);
