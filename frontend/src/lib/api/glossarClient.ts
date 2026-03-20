export interface Word {
    id: number;
    word: string;
    explanation: string;
}

const API_BASE = '/api';

export const GlossarClient = {
    async createWord(word: string, explanation: string) {
        const response = await fetch(`${API_BASE}/words`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ word, explanation })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to create word: ${response.status} ${errorText}`);
        }

        return response.json() as Promise<Word>;
    }
};
