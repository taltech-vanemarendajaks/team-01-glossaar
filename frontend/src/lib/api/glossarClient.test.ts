import { afterEach, describe, expect, it, vi } from 'vitest';
import { GlossarClient, type QuizQuestion } from './glossarClient';

const sampleQuestion: QuizQuestion = {
    wordId: 1,
    word: 'example',
    options: ['A', 'B', 'C', 'D'],
    correctIndex: 2
};

function mockQuizFetch() {
    const fetchMock = vi.fn().mockResolvedValue({
        ok: true,
        json: async () => [sampleQuestion]
    });
    vi.stubGlobal('fetch', fetchMock);
    return fetchMock;
}

describe('GlossarClient.getQuizQuestion', () => {
    afterEach(() => {
        vi.unstubAllGlobals();
        vi.restoreAllMocks();
    });

    it('requests unfiltered quiz when category is not selected', async () => {
        const fetchMock = mockQuizFetch();

        await GlossarClient.getQuizQuestion();

        expect(fetchMock).toHaveBeenCalledWith('/api/quiz?size=1', {
            credentials: 'include'
        });
    });

    it('requests category-filtered quiz when category is selected', async () => {
        const fetchMock = mockQuizFetch();

        await GlossarClient.getQuizQuestion(7);

        expect(fetchMock).toHaveBeenCalledWith('/api/quiz?size=1&categoryId=7', {
            credentials: 'include'
        });
    });
});
