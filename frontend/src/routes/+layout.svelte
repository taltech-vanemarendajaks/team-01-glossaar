<script lang="ts">
    import './layout.css';
    import favicon from '$lib/assets/favicon.svg';
    import Navigation from '$lib/components/Navigation.svelte';
    import { auth, isAuthenticated, isLoading, authData } from '$lib/stores/auth';
    import { onMount } from 'svelte';
    import * as Avatar from '$lib/components/ui/avatar/index';

    onMount(async () => {
        await auth.init();
        if (!$isAuthenticated) {
            if (!window.location.href.endsWith('/login')) {
                window.location.href = '/login';
            }
        }
    });

    let { children } = $props();
</script>

<svelte:head>
    <link rel="icon" href={favicon} />
</svelte:head>

<div class="mx-auto my-4 h-full w-full max-w-[24.5rem] gap-2 rounded-md border p-1">
    {#if !$isLoading}
        <div class="flex flex-row justify-between items-center">
            <Navigation />
            {#if $isAuthenticated}
                <Avatar.Root class="m-1">
                    <Avatar.Image src={$authData?.avatarUrl} alt="User Avatar" />
                    <Avatar.Badge class="bg-white">
                        <img src={`/${$authData?.authProvider?.toLowerCase()}-logo.svg`} alt="user-auth-provider">
                    </Avatar.Badge>
                </Avatar.Root>
            {/if}
        </div>
        <main class="mt-2 rounded-md border p-2">
            {@render children()}
        </main>
    {/if}
</div>
