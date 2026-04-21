<script lang="ts">
    import { onMount } from 'svelte';
    import { Check } from '@lucide/svelte';
    import { GlossarClient, type QuizQuestion } from '$lib/api/glossarClient';
    import Button from '$lib/components/ui/button/button.svelte';

    type Status = 'loading' | 'ready' | 'empty' | 'submitting' | 'error';

    let status: Status = $state('loading');
    let question: QuizQuestion | null = $state(null);
    let selected: number | undefined = $state(undefined);
    let error: string | null = $state(null);
    let submitError: string | null = $state(null);

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
        submitError = null;
        selected = undefined;
        question = null;
        try {
            question = await GlossarClient.getQuizQuestion() ?? null;
            status = question ? 'ready' : 'empty';
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load question';
            status = 'error';
        }
    }

    async function next() {
        if (!question || selected === undefined || status === 'submitting') return;
        status = 'submitting';
        submitError = null;
        try {
            await GlossarClient.submitQuizAnswer(question.wordId, selected === question.correctIndex);
        } catch (e) {
            submitError = e instanceof Error ? e.message : 'Failed to save answer';
        }
        await loadQuestion();
    }

    onMount(() => loadQuestion());
</script>

<div class="space-y-6">
    {#if status === 'loading'}
        <div class="py-12 text-center text-sm text-zinc-500">Loading...</div>

    {:else if status === 'error'}
        <div class="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-center text-sm text-red-600">
            {error}
        </div>
        <Button
            on:click={loadQuestion}
            variant="outline"
            className="w-full"
        >
            Try again
        </Button>

    {:else if status === 'empty'}
        <div class="py-12 text-center text-sm text-zinc-500">
            Add some words to your dictionary to start quizzing!
        </div>

    {:else if question}
        <p class="text-center text-base text-zinc-500">
            What is: <span class="text-lg font-semibold text-zinc-900 underline">{question.word}</span>
        </p>

        <div class="grid grid-cols-2 gap-2">
            {#each question.options as option, idx (idx)}
                <button
                    onclick={() => { selected = idx; }}
                    disabled={isAnswered}
                    class="relative flex aspect-square cursor-pointer items-center justify-center rounded-xl p-3 text-center text-sm transition-colors disabled:cursor-default {cardClass(idx)}"
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

        {#if submitError}
            <div class="rounded-xl border border-amber-200 bg-amber-50 px-3 py-2 text-center text-xs text-amber-700">
                Answer may not have been saved — {submitError}
            </div>
        {/if}

        <Button
            on:click={next}
            disabled={!isAnswered || status === 'submitting'}
            variant="outline"
            className="w-full"
        >
            {status === 'submitting' ? 'Saving...' : 'Next'}
        </Button>
    {/if}
</div>
