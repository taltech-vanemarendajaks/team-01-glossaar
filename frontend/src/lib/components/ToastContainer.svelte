<script lang="ts">
    import { toast } from '$lib/stores/toast';
    import { tv } from 'tailwind-variants';
    import Button from './ui/button/button.svelte';
    import { X } from '@lucide/svelte';

    // TODO: Add some fade-in/out animation to the toast

    const toastVariants = tv({
        base: 'absolute flex flex-row justify-between items-center bg-white shadow-sm rounded-md border gap-1 min-h-10 w-full z-100 p-2 pr-8',
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
  <div class={toastVariants({ type: $toast.type })}>
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
