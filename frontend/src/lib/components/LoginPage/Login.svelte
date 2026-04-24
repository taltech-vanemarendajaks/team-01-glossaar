
<script lang="ts">
    import { Button } from "$lib/components/ui/button/index.js";
    import { _ } from 'svelte-i18n';

    type Provider = {
        name: string;
        href: string;
        icon: string;
    }

    const providers: Provider[] = [
        { name: 'Google', href: '/login/google', icon: '/google-logo.svg' },
        { name: 'GitHub', href: '/login/github', icon: '/github-logo.svg' },
    ];
</script>

<div class="flex flex-col gap-2">
    {#each providers as provider (provider.name)}
        <Button variant="outline" href={provider.href}>
            <img src={provider.icon} alt={provider.name + " logo"} class="w-5 h-5" />
            {$_('login.loginWith', { values: { provider: provider.name } })}
        </Button>
    {/each}
</div>
