<script lang="ts">
    type Props = {
        title: string;
        subtitle?: string;
    };

    let {
        title,
        subtitle
    }: Props  = $props();
</script>

<div class="flex flex-col gap-1">
    <h1 class="font-bold sm:text-2xl text-xl ">{title}</h1>
    {#if subtitle}
        <p class="text-sm text-zinc-600">{subtitle}</p>
    {/if}
</div>