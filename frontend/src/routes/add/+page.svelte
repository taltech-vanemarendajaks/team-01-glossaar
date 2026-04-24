<script lang="ts">
    import PageHeader from '$lib/components/PageHeader.svelte';
    import AddForm from '$lib/components/AddPage/AddForm.svelte';
    import { _ } from 'svelte-i18n';
</script>

<PageHeader title={$_('pages.add.title')} subtitle={$_('pages.add.subtitle')} />
<AddForm />
