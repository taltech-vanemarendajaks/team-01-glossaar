import { GlossarClient } from '$lib/api/glossarClient';

export async function logout() {
    try {
        await GlossarClient.logout();
    } catch (err) {
        throw new Error('Could not log out', { cause: err });
    } finally {
         window.location.reload();
    }
}