export interface Word {
    id: number;
    word: string;
    explanation: string;
    categoryId?: number;
    categoryName: string;
}

export interface GetWordsResponse {
    items: Word[];
    totalItems: number;
    totalPages: number;
    page: number;
    size: number;
    hasNext: boolean;
    hasPrevious: boolean;
    search: string;
    sortBy: string;
    sortDir: string;
}

export interface Category {
    id: number;
    name: string;
    wordCount: number;
}

export interface QuizQuestion {
  wordId: number;
  word: string;
  options: string[];
  correctIndex: number;
}

export interface ExplanationGroup {
    groupNumber: number;
    explanations: string[];
}

export interface MeResponse {
    user: {
        id: number;
        username: string | null;
    };
    authProvider: 'GITHUB' | 'GOOGLE';
    avatarUrl: string | null;
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

    async updateCategory(id: number, name: string): Promise<void> {
        const response = await fetch(`/api/categories/${id}`, {
            method: 'PATCH',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name })
        });

        if (!response.ok) {
            const text = await response.text().catch(() => '');
            throw new Error(`Failed to save category: ${response.status} ${text}`);
        }
    },

    async deleteCategory(id: number): Promise<void> {
        const response = await fetch(`${API_BASE}/categories/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            let message = `Failed to delete category (${response.status})`;
            try {
                const data = await response.json();
                if (data?.message) message = data.message;
            } catch {
                const text = await response.text();
                if (text) message = text;
            }
            throw new Error(message);
        }
    },

    async createWord(word: string, explanation: string, categoryName: string) {
        const response = await fetch(`${API_BASE}/words`, {
            method: 'POST',
            credentials: 'include',
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
    },

    async deleteWord(id: number): Promise<void> {
        const response = await fetch(`${API_BASE}/words/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Failed to delete word: ${response.status}`);
        }
    },

    async updateWord(id: number, payload: {
        word: string;
        explanation: string;
        categoryName: string;
    }): Promise<void> {
        const response = await fetch(`${API_BASE}/words/${id}`, {
            method: 'PATCH',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(`Failed to update word: ${response.status}`);
        }
    },


    async getWords(params: {
        search: string;
        page: number;
        size: number;
        sortBy: string;
        sortDir: string;
    }): Promise<GetWordsResponse> {
        const query = new URLSearchParams({
            search: params.search,
            page: String(params.page),
            size: String(params.size),
            sortBy: params.sortBy,
            sortDir: params.sortDir
        });

        const response = await fetch(`${API_BASE}/words?${query.toString()}`, {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch words: ${response.status}`);
        }

        return response.json();
    },

    async getQuizQuestion(userId: number): Promise<QuizQuestion> {
        const response = await fetch(`${API_BASE}/quiz?userId=${userId}&size=1`);
        if (!response.ok) {
            throw new Error(`Failed to fetch quiz question: ${response.status}`);
        }
        const questions: QuizQuestion[] = await response.json();
        return questions[0];
    },

    async submitQuizAnswer(userId: number, wordId: number, correct: boolean): Promise<void> {
        const response = await fetch(`${API_BASE}/quiz`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId,
                answers: [{ wordId, correct }]
            })
        });
        if (!response.ok) {
            throw new Error(`Failed to submit quiz answer: ${response.status}`);
        }
    },

    async fetchEkiExplanations(word: string): Promise<ExplanationGroup[]> {
        const response = await fetch(`${API_BASE}/eki/explanations/${encodeURIComponent(word)}`);
        if (!response.ok) {
            throw new Error(`EKI explanation lookup failed: ${response.status}`);
        }
        const data: { word: string; explanationGroups: ExplanationGroup[] } = await response.json();
        return data.explanationGroups;
    },

    async getMe(): Promise<MeResponse> {
        const response = await fetch(`${API_BASE}/auth/me`);

        if (!response.ok) {
            throw new Error(`Failed to fetch user info: ${response.status}`);
        }

        return response.json();
    },
};
