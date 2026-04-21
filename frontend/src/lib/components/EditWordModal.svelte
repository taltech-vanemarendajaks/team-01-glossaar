<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import Button from '$lib/components/ui/button/button.svelte';

    export let open = false;
    export let title = 'Edit word';
    export let initialWord = '';
    export let initialExplanation = '';
    export let loading = false;
    export let initialCategory = '';
    export let categories: { id: number; name: string }[] = [];

    let word = '';
    let explanation = '';
    let snapshotWord = '';
    let snapshotExplanation = '';

    let categoryName = '';
    let snapshotCategory = '';

    const dispatch = createEventDispatcher<{
        save: { word: string; explanation: string; categoryName: string };
        cancel: void;
    }>();

    $: if (open && (initialWord !== snapshotWord ||
            initialExplanation !== snapshotExplanation ||
            initialCategory !== snapshotCategory)
    ) {
        word = initialWord;
        explanation = initialExplanation;
        categoryName = initialCategory;

        snapshotWord = initialWord;
        snapshotExplanation = initialExplanation;
        snapshotCategory = initialCategory;
    }

    function onCancel() {
        if (!loading) dispatch('cancel');
    }

    function onSubmit(event: Event) {
        event.preventDefault();
        if (loading) return;

        const trimmedWord = word.trim();
        if (!trimmedWord) return;

        dispatch('save', {
            word: trimmedWord,
            explanation: explanation.trim(),
            categoryName
        });
    }

    function onBackdropClick(event: MouseEvent) {
        if (event.target === event.currentTarget) onCancel();
    }
</script>

<svelte:window
        on:keydown={(event) => {
        if (open && event.key === 'Escape') onCancel();
    }}
/>

{#if open}
    <div
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/45 p-4"
        role="dialog"
        aria-modal="true"
        aria-label={title}
        on:click={onBackdropClick}
    >
        <form class="w-full max-w-sm rounded-xl border border-zinc-200 bg-white p-5 shadow-sm" on:submit={onSubmit}>
            <h3 class="text-lg font-semibold text-zinc-900">{title}</h3>

            <div class="mt-4 grid gap-3">
                <label for="edit-word" class="text-sm font-medium text-zinc-700">Word</label>
                <input
                    id="edit-word"
                    class="h-10 rounded-lg border border-zinc-300 px-3 text-sm"
                    bind:value={word}
                    required
                    disabled={loading}
                />

                <label for="edit-explanation" class="text-sm font-medium text-zinc-700">Explanation</label>
                <textarea
                    id="edit-explanation"
                    class="min-h-24 rounded-lg border border-zinc-300 px-3 py-2 text-sm"
                    bind:value={explanation}
                    disabled={loading}
                />

                <label for="edit-category" class="text-sm font-medium text-zinc-700">Category</label>
                <select
                        id="edit-category"
                        class="h-10 rounded-lg border border-zinc-300 px-3 text-sm"
                        bind:value={categoryName}
                        disabled={loading}
                >
                    {#each categories as c}
                        <option value={c.name}>{c.name}</option>
                    {/each}
                </select>
            </div>

            <div class="mt-5 flex justify-end gap-2">
                <Button
                    type="button"
                    variant="outline"
                    on:click={onCancel}
                    disabled={loading}
                >
                    Cancel
                </Button>
                <Button
                    type="submit"
                    disabled={loading || !word.trim()}
                >
                    {#if loading}Saving...{:else}Save{/if}
                </Button>
            </div>
        </form>
    </div>
{/if}