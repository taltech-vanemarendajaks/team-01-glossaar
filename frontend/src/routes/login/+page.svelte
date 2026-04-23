<script lang="ts">
    import PageHeader from '$lib/components/PageHeader.svelte';
    import Login from '$lib/components/LoginPage/Login.svelte';
    import { _ } from 'svelte-i18n';
</script>

<PageHeader title={$_('pages.login.title')} subtitle={$_('pages.login.subtitle')} />
<Login />
