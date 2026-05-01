<script lang="ts">
    import { toast, TOAST_ANIMATION_DURATION } from '$lib/stores/toast';
    import { tv } from 'tailwind-variants';
    import Button from './ui/button/button.svelte';
    import { X } from '@lucide/svelte';
    import { fly } from 'svelte/transition';

    const toastVariants = tv({
        base: 'absolute flex flex-row justify-between items-center bg-white shadow-sm rounded-md border gap-1 min-h-10 w-full z-100 p-2 pr-8 transition-all',
        variants: {
            type: {
                success: 'border-emerald-200 bg-emerald-50 text-emerald-700',
                error: 'border-red-200 bg-red-50 text-red-700',
            }
        },
        defaultVariants: {
            type: 'success'
        }
    });
</script>

{#if $toast}
  <div class={toastVariants({ type: $toast.type })} in:fly={{ y: -200, duration: TOAST_ANIMATION_DURATION }} out:fly={{ y: -200, duration: TOAST_ANIMATION_DURATION }}>
    <div class="flex flex-col">
        <h3>{$toast.title}</h3>
        {#if $toast.subtitle}
            <p class="text-sm">{$toast.subtitle}</p>
        {/if}
    </div>
    <Button on:click={toast.clear} size="xs" variant="ghost" className="hover:bg-transparent absolute top-2 right-1">
        <X />
    </Button>
  </div>
{/if}
