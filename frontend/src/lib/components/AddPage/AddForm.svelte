<div class="flex justify-center">
    <div class="w-full max-w-2xl p-6 rounded-xl border border-gray-200 bg-white shadow-lg">

        <div class="mb-5">
            <label for="category" class="block text-sm font-medium text-gray-700 mb-2">Category</label>

            {#if !addingNew && categories.length > 0}
                <select class="flex h-9 w-full min-w-0 rounded-md border border-input bg-background px-3 py-1 text-base shadow-xs
                ring-offset-background transition outline-none
                focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50
                aria-invalid:border-destructive aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40"
                        bind:value={selectedCategoryName}>
                    <option value="" disabled>Select category</option>
                    {#each categories as category}
                        <option value={category.name}>{category.name}</option>
                    {/each}
                </select>
            {/if}

            {#if addingNew}
                <Input
                        class="flex-1"
                        placeholder="Enter new category"
                        bind:value={newCategoryName}
                />
            {/if}

            <div class="mt-2 flex justify-between items-center">
                <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        on:click={() => {
                            addingNew = !addingNew;
                            if (addingNew) {
                                selectedCategoryName = '';
                            } else {
                                newCategoryName = '';
                            }
                        }}
                >
                    <span class="flex items-center gap-2">
                        {#if addingNew}
                            Cancel
                        {:else}
                            <Plus class="w-4 h-4"/>
                            Add new
                        {/if}
                    </span>
                </Button>

                <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        on:click={() => (manageModalOpen = true)}
                >
                    <span class="flex items-center gap-2">
                        <Pencil class="w-4 h-4"/>
                        Manage categories
                    </span>
                </Button>
            </div>
        </div>


        <div class="mb-5">
            <label for="word" class="block text-sm font-medium text-gray-700 mb-1">Word</label>
            <Input
                    id="word"
                    type="text"
                    bind:value={word}
                    placeholder="Enter the word"
                    class="w-full border-gray-300 focus:ring-blue-400 focus:border-blue-400"
            />
        </div>

        <div class="mb-5">
            <label for="explanation" class="block text-sm font-medium text-gray-700 mb-1">Explanation</label>
            <Textarea
                    id="explanation"
                    bind:value={explanation}
                    placeholder="Enter the explanation or description"
                    class="w-full border-gray-300 focus:ring-blue-400 focus:border-blue-400 rounded-md min-h-[120px]"
            />
        </div>

        {#if word.trim() || explanation.trim() || categoryName}
            <div class="mb-6 p-5 rounded-xl border border-gray-200 bg-white shadow-sm transition-all">
                {#if categoryName}
                    <div class="mb-3">
                <span class="inline-block text-xs font-medium px-2.5 py-1 rounded-full bg-blue-100 text-blue-700">
                    {categoryName}
                </span>
                    </div>
                {/if}

                {#if word.trim()}
                    <p class="text-lg font-semibold text-gray-900">{word}</p>
                {/if}

                {#if explanation.trim()}
                    <p class="mt-2 text text-gray-600 leading-relaxed">{explanation}</p>
                {/if}
            </div>
        {/if}

        <div class="mt-6 flex justify-center">
            <Button
                    variant="default"
                    size="lg"
                    disabled={!word.trim() || !explanation || !(selectedCategoryName || newCategoryName.trim()) || loading}
                    on:click={saveWord}
            >
                {#if loading}Saving...{:else}Submit{/if}
            </Button>
        </div>

    </div>
</div>


{#if manageModalOpen}
    <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
        <div class="bg-white p-6 rounded-xl max-w-md w-full">
            <h2 class="text-lg font-semibold mb-4">Edit Categories</h2>

            <div class="space-y-2 max-h-80 overflow-y-auto">
                {#each categories as category (category.id)}
                    <div class="flex items-center gap-2">
                        <input class="flex-1 border rounded px-2 py-1" bind:value={category.name}/>
                        <Button size="sm" variant="outline" on:click={() => saveCategory(category)}>
                            Save
                        </Button>
                    </div>
                {/each}
            </div>

            <div class="flex justify-end mt-4">
                <Button variant="outline" on:click={closeManageModal}>Close</Button>
            </div>
        </div>
    </div>
{/if}

<script lang="ts">
    import {Textarea} from '$lib/components/ui/textarea';
    import {Input} from '$lib/components/ui/input';
    import {Button} from '$lib/components/ui/button';
    import {GlossarClient} from '$lib/api/glossarClient';
    import {fetchCategories} from '$lib/services/categoryService';
    import {Plus, Pencil} from '@lucide/svelte';
    import {onMount} from 'svelte';

    let word = '';
    let explanation = '';
    let loading = false;

    let categories: { id: number; name: string }[] = [];
    let selectedCategoryName = '';
    let newCategoryName = '';
    let addingNew = false;

    let manageModalOpen = false;

    $: categoryName = addingNew ? newCategoryName.trim() : selectedCategoryName;

    onMount(async () => {
        try {
            await reloadCategories();
            if (categories.length === 0) {
                addingNew = true;
            }
        } catch (err) {
            alert('Failed to load categories');
            console.error(err);
        }
    });

    async function saveCategory(category: { id: number; name: string }) {
        if (!category.id) {
            alert('Invalid category ID');
            return;
        }
        try {
            await GlossarClient.updateCategory(category.id, category.name);
            alert('Category updated!');
        } catch (err) {
            alert(err instanceof Error ? err.message : 'Failed to update category');
        }
    }

    async function closeManageModal() {
        manageModalOpen = false;
        try {
            await reloadCategories();
        } catch (err) {
            alert('Failed to reload categories');
        }
    }

    async function saveWord() {
        loading = true;
        try {
            const finalCategoryName = addingNew ? newCategoryName.trim() : selectedCategoryName;

            if (!finalCategoryName) {
                alert('Please select or enter a category.');
                return;
            }

            await GlossarClient.createWord(word.trim(), explanation.trim(), finalCategoryName);

            // Reset form
            word = '';
            explanation = '';
            selectedCategoryName = '';
            newCategoryName = '';
            addingNew = false;

            try {
                await reloadCategories();
            } catch (err) {
                alert('Failed to reload categories');
            }

            alert('Word saved successfully!');
        } catch (err) {
            alert('Error saving word.');
        } finally {
            loading = false;
        }
    }

    async function reloadCategories() {
        categories = await fetchCategories();
    }
</script>

<style>
    :global(body) {
        background-color: #f3f4f6;
    }
</style>