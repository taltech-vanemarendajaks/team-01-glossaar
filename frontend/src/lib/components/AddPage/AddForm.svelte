<svelte:window on:click={handleWindowClick} />

<div class="flex justify-center">
    <div class="w-full max-w-2xl p-6 rounded-xl border border-gray-200 bg-white shadow-sm">

        <div class="mb-5">
            <label for="category" class="block text-sm font-medium text-gray-700 mb-2">Category</label>

            {#if !addingNew && categories.length > 0}
                <select class="flex h-9 w-full min-w-0 rounded-md border border-input bg-background px-3 py-1 text-base shadow-sm
                ring-offset-background transition outline-none
                focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50
                aria-invalid:border-destructive aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40"
                        bind:value={selectedCategoryName}>
                    <option value="" disabled>Select category</option>
                    {#each categories as category (category.id)}
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

        <div class="mb-5 mt-8">
            <div class="flex items-center justify-between mb-1">
                <label for="explanation" class="block text-sm font-medium text-gray-700">Explanation</label>
                <div class="relative" bind:this={ekiDropdownRef}>
                    <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            disabled={!word.trim() || ekiLoading}
                            on:click={fetchFromEki}
                    >
                        <span class="flex items-center gap-1.5 text-xs">
                            {#if ekiLoading}
                                <BookOpen class="w-3.5 h-3.5 animate-spin" />
                                Loading…
                            {:else}
                                <BookOpen class="w-3.5 h-3.5" />
                                EKI explanation (ET)
                            {/if}
                        </span>
                    </Button>
                    {#if ekiError || ekiExplanations.length > 0}
                        <div class="absolute right-0 top-full mt-1 w-80 max-h-60 overflow-y-auto rounded-lg border bg-white p-2 shadow-sm z-50">
                            {#if ekiError}
                                <p class="text-xs text-red-500 p-2">{ekiError}</p>
                            {:else}
                                {#each ekiExplanations as ekiGroup, i (ekiGroup.groupNumber)}
                                    {#if i > 0}
                                        <hr class="my-1 border-gray-200" />
                                    {/if}
                                    {#if ekiExplanations.length > 1}
                                        <p class="text-xs font-medium text-gray-500 px-2 pt-1 pb-0.5">{word.trim()}<sup>{ekiGroup.groupNumber}</sup></p>
                                    {/if}
                                    {#each ekiGroup.explanations as explanation, j (j)}
                                        <button
                                            type="button"
                                            class="w-full text-left px-3 py-2 text-sm rounded hover:bg-blue-50 hover:text-blue-700 transition-colors"
                                            on:click={() => selectExplanation(explanation)}
                                        >
                                            <span class="text-gray-400 mr-1">{j + 1}.</span>{explanation}
                                        </button>
                                    {/each}
                                {/each}
                            {/if}
                        </div>
                    {/if}
                </div>
            </div>
            <Textarea
                    id="explanation"
                    bind:value={explanation}
                    placeholder="Enter the explanation or description"
                    class="w-full border-gray-300 focus:ring-blue-400 focus:border-blue-400 rounded-md min-h-[120px]"
            />
        </div>

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
        <div class="bg-white p-6 rounded-md max-w-md w-full">
            <h2 class="text-lg font-semibold mb-4">Edit Categories</h2>

            <div class="space-y-2 max-h-80 overflow-y-auto">
                {#each categories as category (category.id)}
                    <div class="flex items-center gap-2">
                        <input
                                class="flex-1 border rounded px-2 py-1"
                                bind:value={category.name}
                        />

                        <Button size="sm" variant="outline" on:click={() => saveCategory(category)}>
                            Save
                        </Button>

                        <Button
                                size="sm"
                                variant="outline"
                                disabled={category.wordCount > 0}
                                on:click={() => deleteCategory(category)}
                        >
                             <span class="border-red-500 text-red-600 hover:bg-red-50">
                                 <Trash2 class="w-4 h-4"/>
                             </span>
                        </Button>

                    </div>

                    {#if category.wordCount > 0}
                        <p class="text-xs text-gray-500 ml-1">
                            Used in {category.wordCount} word(s)
                        </p>
                    {/if}
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
    import {GlossarClient, type ExplanationGroup} from '$lib/api/glossarClient';
    import {fetchCategories} from '$lib/services/categoryService';
    import {Plus, Pencil, Trash2, BookOpen} from '@lucide/svelte';
    import {onMount} from 'svelte';

    let word = '';
    let explanation = '';
    let loading = false;
    let ekiLoading = false;
    let ekiError = '';
    let ekiExplanations: ExplanationGroup[] = [];
    let ekiDropdownRef: HTMLDivElement;

    let categories: { id: number; name: string; wordCount: number }[] = [];
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
            console.error('Failed to load categories');
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

    async function fetchFromEki() {
        ekiLoading = true;
        ekiError = '';
        ekiExplanations = [];
        try {
            const explanationGroups = await GlossarClient.fetchEkiExplanations(word.trim());
            const allExplanations = explanationGroups.flatMap(group => group.explanations);
            if (allExplanations.length === 0) {
                ekiError = 'No explanation found in EKI for this word.';
            } else if (allExplanations.length === 1) {
                explanation = allExplanations[0];
            } else {
                ekiExplanations = explanationGroups;
            }
        } catch {
            ekiError = 'EKI search for explanation failed';
        } finally {
            ekiLoading = false;
        }
    }

    function selectExplanation(selected: string) {
        explanation = selected;
        ekiExplanations = [];
    }

    function dismissEki() {
        ekiExplanations = [];
        ekiError = '';
    }

    function handleWindowClick(e: MouseEvent) {
        if (ekiDropdownRef && !ekiDropdownRef.contains(e.target as Node)) {
            dismissEki();
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

    async function deleteCategory(category: { id: number; name: string }) {
        if (!confirm(`Delete category "${category.name}"?`)) return;

        try {
            await GlossarClient.deleteCategory(category.id);
            await reloadCategories();

            alert('Category deleted!');
        } catch (err) {
            alert(err instanceof Error ? err.message : 'Failed to delete category');
        }
    }

    async function reloadCategories() {
        categories = await fetchCategories();
    }
</script>