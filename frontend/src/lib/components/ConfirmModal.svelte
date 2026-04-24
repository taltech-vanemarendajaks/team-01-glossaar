<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import Button from './ui/button/button.svelte';
    import { _ } from 'svelte-i18n';

    export let open = false;
    export let title: string | undefined = undefined;
    export let message = '';
    export let confirmText: string | undefined = undefined;
    export let cancelText: string | undefined = undefined;
    export let loading = false;

    $: resolvedTitle = title ?? $_('confirm.defaultTitle');
    $: resolvedConfirm = confirmText ?? $_('common.confirm');
    $: resolvedCancel = cancelText ?? $_('common.cancel');

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
        aria-label={resolvedTitle}
        on:click={onBackdropClick}
    >
        <div class="w-full max-w-sm rounded-xl border border-zinc-200 bg-white p-5 shadow-sm">
            <h3 class="text-lg font-semibold text-zinc-900">{resolvedTitle}</h3>
            <p class="mt-2 text-sm text-zinc-600">{message}</p>

            <div class="mt-5 flex justify-end gap-2">
                <Button
                    variant="outline"
                    on:click={onCancel}
                    disabled={loading}
                >
                    {resolvedCancel}
                </Button>
                <Button
                    variant="destructive"
                    on:click={onConfirm}
                    disabled={loading}
                >
                    {#if loading}{$_('common.deleting')}{:else}{resolvedConfirm}{/if}
                </Button>
            </div>
        </div>
    </div>
{/if}
