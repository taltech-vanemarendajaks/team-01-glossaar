<script lang="ts">
    import { createEventDispatcher } from 'svelte';

    export let open = false;
    export let title = 'Please confirm';
    export let message = '';
    export let confirmText = 'Confirm';
    export let cancelText = 'Cancel';
    export let loading = false;

    const dispatch = createEventDispatcher<{
        confirm: void;
        cancel: void;
    }>();

    function onCancel() {
        if (!loading) dispatch('cancel');
    }

    function onConfirm() {
        if (!loading) dispatch('confirm');
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
        <div class="w-full max-w-sm rounded-xl border border-zinc-200 bg-white p-5 shadow-sm">
            <h3 class="text-lg font-semibold text-zinc-900">{title}</h3>
            <p class="mt-2 text-sm text-zinc-600">{message}</p>

            <div class="mt-5 flex justify-end gap-2">
                <button
                    type="button"
                    class="h-10 rounded-lg border border-zinc-300 bg-white px-4 text-sm font-medium text-zinc-700 disabled:opacity-60"
                    on:click={onCancel}
                    disabled={loading}
                >
                    {cancelText}
                </button>
                <button
                    type="button"
                    class="h-10 rounded-lg bg-red-600 px-4 text-sm font-semibold text-white disabled:opacity-60"
                    on:click={onConfirm}
                    disabled={loading}
                >
                    {#if loading}Deleting...{:else}{confirmText}{/if}
                </button>
            </div>
        </div>
    </div>
{/if}
