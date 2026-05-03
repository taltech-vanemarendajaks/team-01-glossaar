<script lang="ts">
    import { cn } from "$lib/utils";
    import type { Snippet } from "svelte";

    type Props = {
        className?: string;
        children?: Snippet;
    };


    let {
        className = '',
        children,
    }: Props  = $props();
</script>

<div class={cn(`rounded-md border border-zinc-200 bg-white p-4 shadow-sm ${className}`)}>
    {@render children?.()}
</div>