<script lang="ts">
    import {onMount} from 'svelte';
    import {Pencil, Trash2} from '@lucide/svelte';
    import ConfirmModal from '$lib/components/ConfirmModal.svelte';
    import EditWordModal from '$lib/components/EditWordModal.svelte';
    import {GlossarClient} from "$lib/api/glossarClient";
    import Button from '$lib/components/ui/button/button.svelte';

    type Word = {
        id: number;
        word: string;
        explanation: string | null;
        categoryName: string;
    };

    type Category = {
        id: number;
        name: string;
    };

    let categories: Category[] = [];

    let words: Word[] = [];
    let wordsLoading = false;
    let filterLoading = false;
    let error: string | null = null;
    let success: string | null = null;
    let deleteLoading = false;
    let deleteTarget: Word | null = null;
    let editLoading = false;
    let editTarget: Word | null = null;

    let listSearch = '';
    let page = 0;
    let size = 10;
    let totalItems = 0;
    let totalPages = 0;
    let hasNext = false;
    let hasPrevious = false;
    let sortBy = 'word';
    let sortDir = 'asc';

    async function loadWords(targetPage = page) {
        error = null;
        wordsLoading = true;
        try {
            const data = await GlossarClient.getWords({
                search: listSearch.trim(),
                page: targetPage,
                size,
                sortBy,
                sortDir
            });

            words = data.items;
            totalItems = data.totalItems;
            totalPages = data.totalPages;
            page = data.page;
            hasNext = data.hasNext;
            hasPrevious = data.hasPrevious;
            sortBy = data.sortBy || sortBy;
            sortDir = data.sortDir || sortDir;
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

    function openDeleteModal(item: Word) {
        deleteTarget = item;
    }

    function closeDeleteModal() {
        if (!deleteLoading) {
            deleteTarget = null;
        }
    }

    async function confirmDelete() {
        if (!deleteTarget) return;

        error = null;
        success = null;
        deleteLoading = true;
        const target = deleteTarget;
        const fallbackPage = words.length === 1 && page > 0 ? page - 1 : page;

        try {
            await GlossarClient.deleteWord(target.id);

            deleteTarget = null;
            success = `Deleted "${target.word}"`;
            await loadWords(fallbackPage);
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            deleteLoading = false;
        }
    }

    function openEditModal(item: Word) {
        editTarget = item;
    }

    function closeEditModal() {
        if (!editLoading) {
            editTarget = null;
        }
    }

    async function saveEdit(event: CustomEvent<{ word: string; explanation: string; categoryName: string }>) {

        if (!editTarget) return;

        error = null;
        success = null;
        editLoading = true;
        const target = editTarget;
        const payload = event.detail;

        try {
            await GlossarClient.updateWord(target.id, {
                word: payload.word,
                explanation: payload.explanation,
                categoryName: payload.categoryName
            });

            editTarget = null;
            success = `Updated "${payload.word}"`;
            await loadWords(page);
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            editLoading = false;
        }
    }

    onMount(async () => {
        await loadWords(0);
        categories = await GlossarClient.getCategories();
    });

</script>

<div>
    {#if error}
        <div class="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
    {/if}

    {#if success}
        <div class="mb-4 rounded-md border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{success}</div>
    {/if}

    <form on:submit|preventDefault={applyFilter} class="rounded-md border border-zinc-200 bg-white p-4 shadow-sm flex flex-col gap-3">
        <h2 class="text-base font-semibold">Search</h2>

        <input bind:value={listSearch} placeholder="search by word/explanation"
        class="h-10 rounded-lg border border-zinc-300 px-3 text-sm w-full"/>

        <div class="grid gap-3 grid-cols-2">
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
        </div>

        <Button type="submit" size="lg" className="self-end" disabled={filterLoading || wordsLoading || listSearch?.length === 0} >
            Search
        </Button>
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
            <div class="rounded-lg border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm text-zinc-700">Loading words...
            </div>
        {:else if words.length === 0}
            <div class="rounded-lg border border-dashed border-zinc-300 bg-zinc-50 px-4 py-6 text-center text-sm text-zinc-600">
                No words yet.
            </div>
        {:else}
            <ul class="divide-y divide-zinc-200 overflow-hidden rounded-xl border border-zinc-200">
                {#each words as item (item.id)}
                    <li class="px-4 py-3">
                        <div class="flex items-center justify-between gap-3">
                            <div class="text-sm font-semibold text-zinc-900">{item.word}</div>
                            <div class="flex items-center gap-1">
                                <Button
                                        size="xs"
                                        variant="ghost"
                                        on:click={() => openEditModal(item)}
                                >
                                    <Pencil />
                                </Button>
                                <Button
                                        size="xs"
                                        variant="ghost"
                                        className="text-red-500 hover:bg-red-50 hover:text-red-600"
                                        on:click={() => openDeleteModal(item)}
                                >
                                    <Trash2 />
                                </Button>
                            </div>
                        </div>
                        <div class="mt-1 text-sm text-zinc-600">
                            {item.explanation || 'No explanation'}
                        </div>
                        <div class="mt-3">
                            <span class="rounded-full bg-blue-100 px-3 py-0.5 text-xs text-zinc-600">
                                {item.categoryName}
                            </span>
                        </div>
                    </li>
                {/each}
            </ul>

            <div class="mt-4 flex items-center justify-between">
                <Button variant="outline" on:click={previousPage} disabled={!hasPrevious || wordsLoading}>
                    Prev
                </Button>
                <div class="text-sm text-zinc-600">Showing {words.length} item(s)</div>
                <Button variant="outline" on:click={nextPage} disabled={!hasNext || wordsLoading}>
                    Next
                </Button>
            </div>
        {/if}
    </div>
</div>

<ConfirmModal
        open={deleteTarget !== null}
        title="Delete word"
        message={`Are you sure you want to delete word: "${deleteTarget?.word ?? ''}"?`}
        confirmText="Delete"
        cancelText="Cancel"
        loading={deleteLoading}
        on:cancel={closeDeleteModal}
        on:confirm={confirmDelete}
/>

<EditWordModal
        open={editTarget !== null}
        title="Edit word"
        initialWord={editTarget?.word ?? ''}
        initialExplanation={editTarget?.explanation ?? ''}
        initialCategory={editTarget?.categoryName ?? ''}
        categories={categories}
        loading={editLoading}
        on:cancel={closeEditModal}
        on:save={saveEdit}
/>
