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
const VALIDATION_PREFIX = 'ValidationException.';

export class ValidationError extends Error {
    readonly key: string;
    readonly args: string[];
    constructor(key: string, args: string[]) {
        super(key);
        this.name = 'ValidationError';
        this.key = key;
        this.args = args;
    }
}

async function throwIfError(response: Response, fallbackKey: string): Promise<void> {
    if (response.ok) return;

    let body: unknown = null;
    try {
        body = await response.json();
    } catch {
        body = null;
    }

    const error = (body as { error?: string } | null)?.error;
    if (typeof error === 'string' && error.startsWith(VALIDATION_PREFIX)) {
        const key = error.slice(VALIDATION_PREFIX.length);
        const args = (body as { args?: unknown }).args;
        throw new ValidationError(key, Array.isArray(args) ? args.map(String) : []);
    }

    throw new ValidationError(fallbackKey, [String(response.status)]);
}

export const GlossarClient = {
    async getCategories(): Promise<Category[]> {
        const response = await fetch(`${API_BASE}/categories`);
        await throwIfError(response, 'categories: loadFailed');
        return response.json();
    },

    async updateCategory(id: number, name: string): Promise<void> {
        const response = await fetch(`/api/categories/${id}`, {
            method: 'PATCH',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name })
        });
        await throwIfError(response, 'category: updateFailed');
    },

    async deleteCategory(id: number): Promise<void> {
        const response = await fetch(`${API_BASE}/categories/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        await throwIfError(response, 'category: deleteFailed');
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
        await throwIfError(response, 'word: createFailed');
        return response.json() as Promise<Word>;
    },

    async deleteWord(id: number): Promise<void> {
        const response = await fetch(`${API_BASE}/words/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        await throwIfError(response, 'word: deleteFailed');
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
        await throwIfError(response, 'word: updateFailed');
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
        await throwIfError(response, 'words: loadFailed');
        return response.json();
    },

    async getQuizQuestion(categoryId?: number): Promise<QuizQuestion> {
        const query = new URLSearchParams({ size: '1' });
        if (categoryId !== undefined) {
            query.set('categoryId', String(categoryId));
        }

        const response = await fetch(`${API_BASE}/quiz?${query.toString()}`, {
            credentials: 'include'
        });
        await throwIfError(response, 'quiz: loadFailed');
        const questions: QuizQuestion[] = await response.json();
        return questions[0];
    },

    async submitQuizAnswer(wordId: number, correct: boolean): Promise<void> {
        const response = await fetch(`${API_BASE}/quiz`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                answers: [{ wordId, correct }]
            })
        });
        await throwIfError(response, 'quiz: submitFailed');
    },

    async fetchEkiExplanations(word: string): Promise<ExplanationGroup[]> {
        const response = await fetch(`${API_BASE}/eki/explanations/${encodeURIComponent(word)}`);
        await throwIfError(response, 'eki: lookupFailed');
        const data: { word: string; explanationGroups: ExplanationGroup[] } = await response.json();
        return data.explanationGroups;
    },

    async fetchWordnikExplanations(word: string): Promise<ExplanationGroup[]> {
        const response = await fetch(`${API_BASE}/wordnik/explanations/${encodeURIComponent(word)}`);
        await throwIfError(response, 'wordnik: lookupFailed');
        const data: { word: string; explanationGroups: ExplanationGroup[] } = await response.json();
        return data.explanationGroups;
    },

    async getMe(): Promise<MeResponse> {
        const response = await fetch(`${API_BASE}/auth/me`, {
            credentials: 'include'
        });
        await throwIfError(response, 'auth: meFailed');
        return response.json();
    },
};
