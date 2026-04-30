<svelte:window on:click={handleWindowClick} />

<div class="flex justify-center">
    <div class="w-full max-w-2xl p-6 rounded-xl border border-gray-200 bg-white shadow-sm">

        <div class="mb-5">
            <label for="category" class="block text-sm font-medium text-gray-700 mb-2">{$_('add.category')}</label>

            {#if !addingNew && categories.length > 0}
                <select class="flex h-9 w-full min-w-0 rounded-md border border-input bg-background px-3 py-1 text-base shadow-sm
                ring-offset-background transition outline-none
                focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50
                aria-invalid:border-destructive aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40"
                        bind:value={selectedCategoryName}>
                    <option value="" disabled>{$_('add.selectCategory')}</option>
                    {#each categories as category (category.id)}
                        <option value={category.name}>{category.name}</option>
                    {/each}
                </select>
            {/if}

            {#if addingNew}
                <Input
                        class="flex-1"
                        placeholder={$_('add.newCategoryPlaceholder')}
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
					{#if addingNew}
						{$_('common.cancel')}
					{:else}
						<Plus />
						{$_('add.addNew')}
					{/if}
                </Button>

                <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        on:click={() => (manageModalOpen = true)}
                >
                    <Pencil />
                    {$_('add.manageCategories')}
                </Button>
            </div>
        </div>


        <div class="mb-5">
            <label for="word" class="block text-sm font-medium text-gray-700 mb-1">{$_('add.word')}</label>
            <Input
                    id="word"
                    type="text"
                    bind:value={word}
                    placeholder={$_('add.wordPlaceholder')}
                    class="w-full border-gray-300 focus:ring-blue-400 focus:border-blue-400"
            />
        </div>

        <div class="mb-5 mt-8">
            <div class="flex items-center justify-between mb-1">
                <label for="explanation" class="block text-sm font-medium text-gray-700">Explanation</label>
                <div class="relative flex gap-2" bind:this={lookupDropdownRef}>
                    {#each locales as localeObj (localeObj.code)}
                        <Button
                                type="button"
                                variant="outline"
                                size="sm"
                                disabled={!word.trim() || lookupLoading}
                                on:click={() => lookUpWord(localeObj)}
                        >
                                {#if lookupLoadingMap[localeObj.code]}
                                    <Loader class="animate-spin" />
                                {:else}
                                    <BookOpen />
                                {/if}
                                {localeObj.code}
                        </Button>
                    {/each}
                    {#if lookupExplanations.length > 0}
                        <div class="absolute right-0 top-full mt-1 w-80 max-h-60 overflow-y-auto rounded-lg border bg-white p-2 shadow-sm z-50">
                            {#each lookupExplanations as group, i (i)}
                                {#if i > 0}
                                    <hr class="my-1 border-gray-200" />
                                {/if}
                                {#if lookupExplanations.length > 1}
                                    <p class="text-xs font-medium text-gray-500 px-2 pt-1 pb-0.5">{word.trim()}<sup>{i + 1}</sup></p>
                                {/if}
                                {#each group.explanations as exp, j (j)}
                                    <button
                                        type="button"
                                        class="w-full text-left px-3 py-2 text-sm rounded hover:bg-blue-50 hover:text-blue-700 transition-colors"
                                        on:click={() => selectExplanation(exp)}
                                    >
                                        <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                                        <span class="text-gray-400 mr-1">{j + 1}.</span><span>{@html exp}</span>
                                    </button>
                                {/each}
                            {/each}
                        </div>
                    {/if}
                </div>
            </div>
            <Textarea
                    id="explanation"
                    bind:value={explanation}
                    placeholder={$_('add.explanationPlaceholder')}
                    class="w-full border-gray-300 focus:ring-blue-400 focus:border-blue-400 rounded-md min-h-[120px]"
            />
        </div>

        <div class="mt-6 flex justify-end">
            <Button
                    variant="default"
                    size="lg"
                    disabled={!word.trim() || !explanation || !(selectedCategoryName || newCategoryName.trim()) || loading || lookupLoading}
                    on:click={saveWord}
            >
                {#if loading}{$_('common.saving')}{:else}{$_('add.submit')}{/if}
            </Button>
        </div>

    </div>
</div>

<!-- TODO: decouple to another component  -->
{#if manageModalOpen}
    <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
        <div class="bg-white p-6 rounded-md max-w-md w-full">
            <h2 class="text-lg font-semibold mb-4">{$_('add.editCategoriesTitle')}</h2>

            <div class="space-y-2 max-h-80 overflow-y-auto">
                {#each categories as category (category.id)}
                    <div class="flex items-center gap-2">
                        <input
                                class="flex-1 border rounded px-2 py-1"
                                bind:value={category.name}
                        />

                        <Button size="sm" variant="outline" on:click={() => saveCategory(category)}>
                            {$_('common.save')}
                        </Button>

                        <!-- TODO: show some tooltip of why it can't be deleted -->
                        <Button
                            size="sm"
                            variant="ghost"
                            className="text-red-500 hover:bg-red-50 hover:text-red-600"
                            disabled={category.wordCount > 0}
                            on:click={() => deleteCategory(category)}
                        >
                            <Trash2 />
                        </Button>

                    </div>

                    {#if category.wordCount > 0}
                        <p class="text-xs text-gray-500 ml-1">
                            {$_('add.usedInWords', { values: { count: category.wordCount } })}
                        </p>
                    {/if}
                {/each}
            </div>

            <div class="flex justify-end mt-4">
                <Button variant="outline" on:click={closeManageModal}>{$_('common.close')}</Button>
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
    import {Plus, Pencil, Trash2, BookOpen, Loader} from '@lucide/svelte';
    import {onMount} from 'svelte';
    import { _ } from 'svelte-i18n';
    import { toast } from '$lib/stores/toast';
    import type { SupportedLocale } from '$lib/i18n';
    import { translateError } from '$lib/i18n/translateError';

    type LocaleObj = {
        code: SupportedLocale;
        fetchAction: (word: string) => Promise<ExplanationGroup[]>;
        source: string
    };

    const locales: LocaleObj[] = [
        { code: 'et', fetchAction: GlossarClient.fetchEkiExplanations, source: 'EKI' },
        { code: 'en', fetchAction: GlossarClient.fetchWordnikExplanations, source: 'Wordnik' },
    ];

    let word = '';
    let explanation = '';
    let loading = false;

    let lookupLoadingMap: { [K in SupportedLocale]: boolean } = {
        et: false,
        en: false,
    };

    $: lookupLoading = Object.values(lookupLoadingMap).some(Boolean);
    let lookupExplanations: ExplanationGroup[] = [];
    let lookupDropdownRef: HTMLDivElement;

    let categories: { id: number; name: string; wordCount: number }[] = [];
    let selectedCategoryName = '';
    let newCategoryName = '';
    let addingNew = false;

    let manageModalOpen = false;

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
            toast.error($_('add.invalidCategoryId'));
            return;
        }

        try {
            await GlossarClient.updateCategory(category.id, category.name);
            toast.success($_('add.categoryUpdated'));
        } catch (err) {
            toast.error(translateError(err, 'category: updateFailed'));
        }
    }

    async function closeManageModal() {
        manageModalOpen = false;
        try {
            await reloadCategories();
        } catch (err) {
            toast.error(translateError(err, 'categories: loadFailed'));
        }
    }

    async function lookUpWord(localeObj: typeof locales[number]) {
        lookupLoadingMap[localeObj.code] = true;
        lookupExplanations = [];

        try {
            const groups = await localeObj.fetchAction(word.trim());

            const allExplanations = groups.flatMap(g => g.explanations);

            // TODO: display a toast/notice instead of setting the error in the dropdown
            if (allExplanations.length === 0) {
                toast.error($_('add.lookupNoResult', { values: { source: localeObj.source } }));
            } else if (allExplanations.length === 1) {
                explanation = allExplanations[0];
            } else {
                lookupExplanations = groups;
            }
        } catch {
            toast.error($_('add.lookupFailed', { values: { source: localeObj.source } }));
        } finally {
            lookupLoadingMap[localeObj.code] = false;
        }
    }

    function selectExplanation(selected: string) {
        explanation = selected;
        lookupExplanations = [];
    }

    function dismissLookup() {
        lookupExplanations = [];
    }

    function handleWindowClick(e: MouseEvent) {
        if (lookupDropdownRef && !lookupDropdownRef.contains(e.target as Node)) {
            dismissLookup();
        }
    }

    async function saveWord() {
        loading = true;
        try {
            const finalCategoryName = addingNew ? newCategoryName.trim() : selectedCategoryName;

            if (!finalCategoryName) {
                toast.error($_('add.selectOrEnterCategory'));
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
                toast.error(translateError(err, 'categories: loadFailed'));
            }

            toast.success($_('add.wordSaved'));
        } catch (err) {
            toast.error(translateError(err, 'word: createFailed'));
        } finally {
            loading = false;
        }
    }

    async function deleteCategory(category: { id: number; name: string }) {
        if (!confirm($_('add.confirmDeleteCategory', { values: { name: category.name } }))) return;

        try {
            await GlossarClient.deleteCategory(category.id);
            await reloadCategories();

            toast.success($_('add.categoryDeleted'));
        } catch (err) {
            toast.error(translateError(err, 'category: deleteFailed'));
        }
    }

    async function reloadCategories() {
        categories = await fetchCategories();
    }
</script>
