<script lang="ts">
    import PageHeader from '$lib/components/PageHeader.svelte';
    import List from '$lib/components/ListPage/List.svelte';
    import { _ } from 'svelte-i18n';
</script>

<PageHeader title={$_('pages.list.title')} subtitle={$_('pages.list.subtitle')} />
<List />
