<script lang="ts">
    import PageHeader from '$lib/components/PageHeader.svelte';
    import Quiz from '$lib/components/QuizPage/Quiz.svelte';
    import { _ } from 'svelte-i18n';
</script>

<PageHeader title={$_('pages.quiz.title')} subtitle={$_('pages.quiz.subtitle')} />
<Quiz />
