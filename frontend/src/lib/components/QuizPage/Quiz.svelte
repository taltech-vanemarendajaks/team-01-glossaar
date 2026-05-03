<script lang="ts">
    import { onMount } from 'svelte';
    import { Check } from '@lucide/svelte';
    import { GlossarClient, type QuizQuestion, type Category } from '$lib/api/glossarClient';
    import Button from '$lib/components/ui/button/button.svelte';
    import { _ } from 'svelte-i18n';
    import { toast } from '$lib/stores/toast';
    import { translateError } from '$lib/i18n/translateError';

    type Status = 'loading' | 'ready' | 'empty' | 'submitting' | 'error';

    let status: Status = $state('loading');
    let question: QuizQuestion | null = $state(null);
    let selected: number | undefined = $state(undefined);
    let error: string | null = $state(null);
    let categories: Category[] = $state([]);
    let selectedCategoryId: number | null = $state(null);
    let categoriesLoading: boolean = $state(true);

    let isAnswered = $derived(selected !== undefined);

    const cardClasses: Record<string, string> = {
        idle: 'border border-zinc-300 bg-white hover:bg-zinc-50',
        correct: 'border-2 border-emerald-500 bg-white',
        wrong: 'border-2 border-red-500 bg-white',
        dimmed: 'border border-zinc-200 bg-white',
    };

    function cardClass(idx: number): string {
        if (!isAnswered) return cardClasses.idle;
        if (idx === question!.correctIndex) return cardClasses.correct;
        if (idx === selected) return cardClasses.wrong;
        return cardClasses.dimmed;
    }

    async function loadQuestion() {
        status = 'loading';
        error = null;
        selected = undefined;
        question = null;
        try {
            question = await GlossarClient.getQuizQuestion(selectedCategoryId ?? undefined) ?? null;
            status = question ? 'ready' : 'empty';
        } catch (e) {
            error = translateError(e, 'request: failed');
            status = 'error';
        }
    }

    async function loadCategories() {
        categoriesLoading = true;
        try {
            categories = await GlossarClient.getCategories();
        } catch (e) {
            error = translateError(e, 'request: failed');
            status = 'error';
        } finally {
            categoriesLoading = false;
        }
    }

    async function handleCategoryChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        selectedCategoryId = target.value === '' ? null : Number(target.value);
        await loadQuestion();
    }

    async function next() {
        if (!question || selected === undefined || status === 'submitting') return;
        status = 'submitting';
        try {
            await GlossarClient.submitQuizAnswer(question.wordId, selected === question.correctIndex);
        } catch (e) {
            toast.error(translateError(e, 'quiz: submitFailed'));
        }
        await loadQuestion();
    }

    onMount(async () => {
        await loadCategories();
        await loadQuestion();
    });
</script>

<div class="space-y-6">
    <div class="space-y-2">
        <label for="quiz-category" class="block text-sm font-medium text-zinc-700">{$_('quiz.category')}</label>
        <select
            id="quiz-category"
            value={selectedCategoryId === null ? '' : String(selectedCategoryId)}
            onchange={handleCategoryChange}
            disabled={categoriesLoading || status === 'submitting'}
            class="h-10 w-full rounded-lg border border-zinc-300 bg-white px-3 text-sm disabled:opacity-50"
        >
            <option value="">{$_('quiz.allCategories')}</option>
            {#each categories as category (category.id)}
                <option value={String(category.id)}>{category.name}</option>
            {/each}
        </select>
    </div>

    {#if status === 'loading'}
        <div class="py-12 text-center text-sm text-zinc-500">{$_('common.loading')}</div>

    {:else if status === 'error'}
        <div class="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-center text-sm text-red-600">
            {error}
        </div>
        <Button
            on:click={loadQuestion}
            variant="outline"
            className="w-full"
        >
            {$_('common.tryAgain')}
        </Button>

    {:else if status === 'empty'}
        <div class="py-12 text-center text-sm text-zinc-500">
            {$_('quiz.empty')}
            {#if selectedCategoryId !== null}
                {$_('quiz.emptyCategory')}
            {:else}
                {$_('quiz.empty')}
            {/if}
        </div>

    {:else if question}
        <p class="text-center text-base text-zinc-500">
            {$_('quiz.whatIs')} <span class="text-lg font-semibold text-zinc-900 underline">{question.word}</span>
        </p>

        <div class="grid grid-cols-2 gap-2">
            {#each question.options as option, idx (idx)}
                <!-- TODO: use card component? -->
                <button
                    onclick={() => { selected = idx; }}
                    disabled={isAnswered}
                    class="relative flex aspect-square cursor-pointer items-center justify-center rounded-xl p-3 text-center text-sm transition-colors disabled:cursor-default shadow-sm {cardClass(idx)}"
                >
                    <span class="line-clamp-6" title={option}>{option}</span>
                    {#if selected === idx}
                        <span
                            class="absolute -top-2 -right-2 flex h-6 w-6 items-center justify-center rounded-full border border-zinc-300 bg-white shadow-sm"
                        >
                            <Check class="h-3 w-3" />
                        </span>
                    {/if}
                </button>
            {/each}
        </div>

        <Button
            on:click={next}
            disabled={!isAnswered || status === 'submitting'}
            variant="outline"
            className="w-full"
        >
            {status === 'submitting' ? $_('common.saving') : $_('list.next')}
        </Button>
    {/if}
</div>
