<div class="flex justify-center">
    <div class="w-full max-w-2xl p-6 rounded-xl border border-gray-200 bg-white shadow-lg">

        <div class="mb-5">
            <label for="category" class="block text-sm font-medium text-gray-700 mb-2">Category</label>

            {#if !addingNew && categories.length > 0}
                <select class="@apply flex h-9 w-full min-w-0 rounded-md border border-input bg-background px-3 py-1 text-base shadow-xs
        ring-offset-background transition outline-none
        focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50
        aria-invalid:border-destructive aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40;"
                        bind:value={selectedCategoryName}>
                    <option value="" disabled selected>Select category</option>
                    {#each categories as category}
                        <option value={category.name}>{category.name}</option>
                    {/each}
                </select>
            {/if}

            <div class="mt-2 flex items-center space-x-2">
                <button
                        type="button"
                        class="text-sm text-blue-500 hover:underline"
                        on:click={() => {
        addingNew = !addingNew;
        if (addingNew) selectedCategoryName = '';
      }}
                >
                    {addingNew ? 'Cancel' : '+ Add new category'}
                </button>

                {#if addingNew}
                    <Input
                            class="flex-1"
                            placeholder="Enter new category"
                            bind:value={newCategoryName}
                    />
                {/if}
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

<script lang="ts">
    import {Textarea} from '$lib/components/ui/textarea';
    import {Input} from '$lib/components/ui/input';
    import {Button} from '$lib/components/ui/button';
    import {GlossarClient} from '$lib/api/glossarClient';
    import {onMount} from 'svelte';

    let word = '';
    let explanation = '';
    let loading = false;

    let categories: { id: number; name: string }[] = [];
    let selectedCategoryName = '';
    let newCategoryName = '';
    let addingNew = false;
    $: categoryName = addingNew ? newCategoryName.trim() : selectedCategoryName;

    onMount(async () => {
        try {
            categories = await GlossarClient.getCategories();
            if (categories.length === 0) {
                addingNew = true;
            }
        } catch (err) {
            console.error('Failed to fetch categories', err);
        }
    });

    async function saveWord() {
        loading = true;
        try {
            const categoryName = addingNew ? newCategoryName.trim() : selectedCategoryName;

            if (!categoryName) {
                alert('Please select or enter a category.');
                return;
            }

            await GlossarClient.createWord(word.trim(), explanation.trim(), categoryName.trim());

            // Reset form
            word = '';
            explanation = '';
            selectedCategoryName = '';
            newCategoryName = '';
            addingNew = false;

            alert('Word saved successfully!');
        } catch (err) {
            console.error(err);
            alert('Error saving word.');
        } finally {
            loading = false;
        }
    }
</script>

<style>
    :global(body) {
        background-color: #f3f4f6;
    }
</style>