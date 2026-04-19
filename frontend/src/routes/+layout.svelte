<script lang="ts">
    import './layout.css';
    import favicon from '$lib/assets/favicon.svg';
    import Navigation from '$lib/components/Navigation.svelte';
    import { auth, isAuthenticated } from '$lib/stores/auth';
    import { onMount } from 'svelte';
    import UserAvatar from '$lib/components/UserAvatar.svelte';
    import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
    import { logout } from '$lib/services/authService';
    import { goto } from '$app/navigation';

    onMount(async () => {
        await auth.init();
        if ($isAuthenticated) {
            if (window.location.href === '/') {
                goto('/add');
            }
        } else {
            // TODO: needs to be improved. If navigating directly to protected page, it will briefly flash before redirecting
            if (!window.location.href.endsWith('/login')) {
                goto('/login');
            }
        }
    });

    let { children } = $props();
</script>

<svelte:head>
    <link rel="icon" href={favicon} />
    <title>Glossaar</title>
</svelte:head>

<div class="mx-auto my-4 h-full w-full max-w-[24.5rem] gap-2 rounded-md border p-1">
    <div class="flex flex-row justify-between items-center">
        <Navigation />
        {#if $isAuthenticated}
            <DropdownMenu.Root>
                <DropdownMenu.Trigger class="hover:cursor-pointer" >
                    <UserAvatar />
                </DropdownMenu.Trigger>
                <DropdownMenu.Content class="w-28" align="end">
                    <DropdownMenu.Item class="hover:cursor-pointer" variant="destructive" onSelect={logout}>Log out</DropdownMenu.Item>
                </DropdownMenu.Content>
            </DropdownMenu.Root>
        {/if}
    </div>
    <main class="mt-2 rounded-md border p-2">
        {@render children()}
    </main>
</div>
