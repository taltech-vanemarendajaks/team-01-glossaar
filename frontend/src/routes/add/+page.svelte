<script lang="ts">
    import AddForm from '$lib/components/AddPage/AddForm.svelte';
</script>

<main class="mx-auto w-full max-w-4xl px-4 py-6 sm:py-10">
    <h1 class="text-xl font-semibold tracking-tight sm:text-2xl">Create word</h1>
    <AddForm />
</main>

<style>
    main {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }
</style>