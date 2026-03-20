<div class="flex justify-center my-12 px-4">
    <div class="w-full max-w-2xl p-6 rounded-xl border border-gray-200 bg-white shadow-lg">

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

        {#if word || explanation}
            <div class="mb-5 p-4 border border-gray-200 rounded-md bg-gray-50">
                <p class="text-sm text-gray-600"><span class="font-semibold">Word:</span> {word}</p>
                <p class="text-sm text-gray-600 mt-1"><span class="font-semibold">Explanation:</span> {explanation}</p>
            </div>
        {/if}

        <div class="mt-6 flex justify-center">
            <Button
                    variant="default"
                    size="lg"
                    disabled={!word || !explanation || loading}
                    on:click={saveWord}
            >
                {#if loading}Saving...{:else}Submit{/if}
            </Button>
        </div>

    </div>
</div>

<script lang="ts">
    import { Textarea } from '$lib/components/ui/textarea';
    import { Input } from '$lib/components/ui/input';
    import { Button } from '$lib/components/ui/button';
    import { GlossarClient } from '$lib/api/glossarClient';

    let word = '';
    let explanation = '';
    let loading = false;

    async function saveWord() {
        loading = true;
        try {
            const saved = await GlossarClient.createWord(word, explanation);
            console.log('Saved word:', saved);
            word = '';
            explanation = '';
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