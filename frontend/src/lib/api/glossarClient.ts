export interface Word {
    id: number;
    word: string;
    explanation: string;
    categoryId?: number;
}

export interface Category {
    id: number;
    name: string;
}

const API_BASE = '/api';

export const GlossarClient = {
    async getCategories(): Promise<Category[]> {
        const response = await fetch(`${API_BASE}/categories`);
        if (!response.ok) {
            throw new Error(`Failed to fetch categories: ${response.status}`);
        }
        return response.json();
    },

    async createWord(word: string, explanation: string, categoryName: string) {
        const response = await fetch(`${API_BASE}/words`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                word,
                explanation,
                categoryName
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to create word: ${response.status} ${errorText}`);
        }

        return response.json() as Promise<Word>;
    }
};
