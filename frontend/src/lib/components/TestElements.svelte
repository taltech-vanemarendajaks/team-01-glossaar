<script lang="ts">
    import { onMount } from 'svelte';

    type Word = {
        id: number;
        word: string;
        explanation: string | null;
    };

    type GetWordsResponse = {
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
    };

    let words: Word[] = [];
    let wordsLoading = false;
    let busy = false;
    let filterLoading = false;

    let error: string | null = null;
    let success: string | null = null;

    let newWord = '';
    let newExplanation = '';

    let patchId = '';
    let patchWord = '';
    let patchExplanation = '';

    let deleteId = '';
    let listSearch = '';
    let page = 0;
    let size = 10;
    let totalItems = 0;
    let totalPages = 0;
    let hasNext = false;
    let hasPrevious = false;
    let sortBy = 'word';
    let sortDir = 'asc';

    function clearMessages() {
        error = null;
        success = null;
    }

    async function loadWords(targetPage = page) {
        clearMessages();
        wordsLoading = true;
        try {
            const params = new URLSearchParams({
                search: listSearch.trim(),
                page: String(targetPage),
                size: String(size),
                sortBy,
                sortDir
            });
            const res = await fetch(`/api/words?${params.toString()}`);
            if (!res.ok) throw new Error(`GET /api/words -> HTTP ${res.status}`);
            const payload = (await res.json()) as GetWordsResponse;
            words = payload.items;
            totalItems = payload.totalItems;
            totalPages = payload.totalPages;
            page = payload.page;
            hasNext = payload.hasNext;
            hasPrevious = payload.hasPrevious;
            sortBy = payload.sortBy || sortBy;
            sortDir = payload.sortDir || sortDir;
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            wordsLoading = false;
        }
    }

    async function applyFilter() {
        filterLoading = true;
        try {
            await loadWords(0);
        } finally {
            filterLoading = false;
        }
    }

    async function nextPage() {
        if (hasNext) {
            await loadWords(page + 1);
        }
    }

    async function previousPage() {
        if (hasPrevious) {
            await loadWords(page - 1);
        }
    }

    async function createWord() {
        clearMessages();
        const word = newWord.trim();
        const explanation = newExplanation.trim();

        if (!word) {
            error = 'Word is required.';
            return;
        }

        busy = true;
        try {
            const res = await fetch('/api/words', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ word, explanation })
            });

            if (!res.ok) {
                const text = await res.text().catch(() => '');
                throw new Error(text || `POST /api/words -> HTTP ${res.status}`);
            }

            const created = (await res.json()) as Word;
            success = `Created #${created.id}: ${created.word}`;
            newWord = '';
            newExplanation = '';
            await loadWords(page);
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            busy = false;
        }
    }

    async function patchWordById() {
        clearMessages();
        const id = Number(patchId);
        const payload: { word?: string; explanation?: string } = {};

        if (!Number.isInteger(id) || id <= 0) {
            error = 'Patch ID must be a positive number.';
            return;
        }

        if (patchWord.trim()) payload.word = patchWord.trim();
        if (patchExplanation.trim()) payload.explanation = patchExplanation.trim();

        if (Object.keys(payload).length === 0) {
            error = 'Provide at least one field to patch.';
            return;
        }

        busy = true;
        try {
            const res = await fetch(`/api/words/${id}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const text = await res.text().catch(() => '');
                throw new Error(text || `PATCH /api/words/${id} -> HTTP ${res.status}`);
            }

            const updated = (await res.json()) as Word;
            success = `Updated #${updated.id}: ${updated.word}`;
            patchId = '';
            patchWord = '';
            patchExplanation = '';
            await loadWords(page);
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            busy = false;
        }
    }

    async function deleteWordById() {
        clearMessages();
        const id = Number(deleteId);

        if (!Number.isInteger(id) || id <= 0) {
            error = 'Delete ID must be a positive number.';
            return;
        }

        busy = true;
        try {
            const res = await fetch(`/api/words/${id}`, { method: 'DELETE' });
            if (!res.ok) {
                const text = await res.text().catch(() => '');
                throw new Error(text || `DELETE /api/words/${id} -> HTTP ${res.status}`);
            }

            success = `Word #${id} deleted successfully`;
            deleteId = '';
            await loadWords(page);
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            busy = false;
        }
    }

    onMount(() => loadWords(0));
</script>

<div class="mx-auto w-full max-w-4xl px-4 py-6 sm:py-10">
    <div class="mb-6">
        <h1 class="text-xl font-semibold tracking-tight sm:text-2xl">Words API tester</h1>
        <p class="mt-1 text-sm text-zinc-600">
            Test <code>POST/GET/PATCH/DELETE /api/words</code> from frontend proxy.
        </p>
    </div>

    {#if error}
        <div class="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
    {/if}

    {#if success}
        <div class="mb-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{success}</div>
    {/if}

    <div class="grid gap-4">
        <form on:submit|preventDefault={createWord} class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
            <h2 class="mb-3 text-base font-semibold">Create word</h2>
            <div class="grid gap-3">
                <input bind:value={newWord} placeholder="word" class="h-10 rounded-lg border border-zinc-300 px-3 text-sm" />
                <textarea bind:value={newExplanation} placeholder="explanation" class="min-h-24 rounded-lg border border-zinc-300 px-3 py-2 text-sm"></textarea>
                <button type="submit" disabled={busy} class="h-10 rounded-lg bg-zinc-900 text-sm font-semibold text-white disabled:opacity-60">POST /api/words</button>
            </div>
        </form>

        <form on:submit|preventDefault={patchWordById} class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
            <h2 class="mb-3 text-base font-semibold">Patch word</h2>
            <div class="grid gap-3">
                <input bind:value={patchId} placeholder="id (required)" class="h-10 rounded-lg border border-zinc-300 px-3 text-sm" />
                <input bind:value={patchWord} placeholder="new word (optional)" class="h-10 rounded-lg border border-zinc-300 px-3 text-sm" />
                <textarea bind:value={patchExplanation} placeholder="new explanation (optional)" class="min-h-24 rounded-lg border border-zinc-300 px-3 py-2 text-sm"></textarea>
                <button type="submit" disabled={busy} class="h-10 rounded-lg bg-zinc-900 text-sm font-semibold text-white disabled:opacity-60">PATCH /api/words/:id</button>
            </div>
        </form>

        <form on:submit|preventDefault={deleteWordById} class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
            <h2 class="mb-3 text-base font-semibold">Delete word</h2>
            <div class="grid gap-3">
                <input bind:value={deleteId} placeholder="id (required)" class="h-10 rounded-lg border border-zinc-300 px-3 text-sm" />
                <button type="submit" disabled={busy} class="h-10 rounded-lg bg-red-600 text-sm font-semibold text-white disabled:opacity-60">DELETE /api/words/:id</button>
                <button type="button" on:click={() => loadWords(page)} disabled={wordsLoading} class="h-10 rounded-lg border border-zinc-300 bg-white text-sm font-medium">Refresh current page</button>
            </div>
        </form>
    </div>

    <form on:submit|preventDefault={applyFilter} class="mt-6 rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
        <h2 class="mb-3 text-base font-semibold">Get words options</h2>
        <div class="grid gap-3 sm:grid-cols-2">
            <input bind:value={listSearch} placeholder="search by word/explanation" class="h-10 rounded-lg border border-zinc-300 px-3 text-sm sm:col-span-2" />
            <select bind:value={size} class="h-10 rounded-lg border border-zinc-300 px-3 text-sm">
                <option value={5}>5 per page</option>
                <option value={10}>10 per page</option>
                <option value={20}>20 per page</option>
                <option value={50}>50 per page</option>
            </select>
            <select bind:value={sortBy} class="h-10 rounded-lg border border-zinc-300 px-3 text-sm">
                <option value="word">Sort by word</option>
                <option value="explanation">Sort by explanation</option>
            </select>
            <select bind:value={sortDir} class="h-10 rounded-lg border border-zinc-300 px-3 text-sm">
                <option value="asc">Ascending (ASC)</option>
                <option value="desc">Descending (DESC)</option>
            </select>
            <button type="submit" disabled={filterLoading || wordsLoading} class="h-10 rounded-lg border border-zinc-300 bg-white text-sm font-medium sm:col-span-2">
                GET /api/words
            </button>
        </div>
    </form>

    <div class="mt-6 rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
        <div class="mb-3 flex items-center justify-between">
            <h2 class="text-base font-semibold">Words</h2>
            <span class="text-sm text-zinc-600">
                {#if wordsLoading}
                    Loading...
                {:else}
                    {totalItems} total | page {page + 1} / {Math.max(totalPages, 1)}
                {/if}
            </span>
        </div>

        {#if wordsLoading}
            <div class="rounded-lg border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm text-zinc-700">Loading words...</div>
        {:else if words.length === 0}
            <div class="rounded-lg border border-dashed border-zinc-300 bg-zinc-50 px-4 py-6 text-center text-sm text-zinc-600">No words yet.</div>
        {:else}
            <ul class="divide-y divide-zinc-200 overflow-hidden rounded-xl border border-zinc-200">
                {#each words as item (item.id)}
                    <li class="px-4 py-3">
                        <div class="flex items-center justify-between gap-3">
                            <div class="text-sm font-semibold text-zinc-900">{item.word}</div>
                            <span class="rounded-full bg-zinc-100 px-2.5 py-1 text-xs font-medium text-zinc-700">#{item.id}</span>
                        </div>
                        <div class="mt-1 text-sm text-zinc-600">{item.explanation || 'No explanation'}</div>
                    </li>
                {/each}
            </ul>

            <div class="mt-4 flex items-center justify-between gap-3">
                <button type="button" on:click={previousPage} disabled={!hasPrevious || wordsLoading} class="h-10 rounded-lg border border-zinc-300 bg-white px-4 text-sm font-medium disabled:opacity-50">
                    Prev
                </button>
                <div class="text-sm text-zinc-600">Showing {words.length} item(s)</div>
                <button type="button" on:click={nextPage} disabled={!hasNext || wordsLoading} class="h-10 rounded-lg border border-zinc-300 bg-white px-4 text-sm font-medium disabled:opacity-50">
                    Next
                </button>
            </div>
        {/if}
    </div>
</div>
