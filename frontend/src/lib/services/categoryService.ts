import { GlossarClient } from '$lib/api/glossarClient';

export async function fetchCategories() {
    try {
        return await GlossarClient.getCategories();
    } catch (err) {
        console.error('Failed to fetch categories', err);
        throw new Error('Could not load categories');
    }
}