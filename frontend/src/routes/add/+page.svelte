<script lang="ts">
    import AddForm from '$lib/components/AddPage/AddForm.svelte';
</script>

<main class="p-6 max-w-xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">Create word</h1>
    <AddForm />
</main>

<style>
    main {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }
</style>