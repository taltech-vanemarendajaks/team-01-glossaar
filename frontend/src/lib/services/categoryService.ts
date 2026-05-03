import { GlossarClient } from '$lib/api/glossarClient';

export async function fetchCategories() {
    return GlossarClient.getCategories();
}