
<script lang="ts">
    import { Button } from "$lib/components/ui/button/index.js";

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

<div class="flex flex-col gap-4">
    <h1 class="text-2xl">Login with an OAuth provider</h1>
    <div class="flex flex-col gap-2">
        {#each providers as provider (provider.name)}
            <Button variant="outline" className="w-full" href={provider.href}>
                <img src={provider.icon} alt={provider.name + " logo"} class="w-5 h-5 mr-2" />
                Login with {provider.name}
            </Button>
        {/each}
    </div>
</div>