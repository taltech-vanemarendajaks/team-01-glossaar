<script lang="ts">
    import './layout.css';
    import '$lib/i18n';
    import favicon from '$lib/assets/favicon.svg';
    import Navigation from '$lib/components/Navigation.svelte';
    import { auth, isAuthenticated, isLoading } from '$lib/stores/auth';
    import { onMount } from 'svelte';
    import UserAvatar from '$lib/components/UserAvatar.svelte';
    import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
    import { logout } from '$lib/services/authService';
    import { goto } from '$app/navigation';
    import { locale, _ } from 'svelte-i18n';
    import { buttonVariants } from '$lib/components/ui/button';
    import { setLocale, SUPPORTED_LOCALES } from '$lib/i18n';

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

{#if $isLoading || !$locale}
    <div class="flex items-center justify-center h-screen">
        <p class="text-lg text-zinc-500">{$locale ? $_('common.loading') : 'Loading...'}</p>
    </div>
{:else}
    <div class="mx-auto my-2 h-full w-full max-w-[24.5rem] flex flex-col p-2 rounded-md border">
        <div class="flex flex-row justify-between items-center sticky top-1 bg-white shadow-sm rounded-md border pr-1 gap-1">
            <Navigation />
            <div class="flex flex-row items-center gap-1">
                <DropdownMenu.Root>
                    <DropdownMenu.Trigger
                        class={buttonVariants({ variant: 'ghost', size: 'sm' }) + ' hover:cursor-pointer'}
                    >
                        {$_('lang.label', { values: { code: ($locale ?? '').slice(0, 2) } })}
                    </DropdownMenu.Trigger>
                    <DropdownMenu.Content align="end">
                        {#each SUPPORTED_LOCALES as code (code)}
                            <DropdownMenu.Item
                                class="hover:cursor-pointer"
                                onSelect={() => setLocale(code)}
                            >
                                {$_(`lang.${code}`)} ({code.toUpperCase()})
                            </DropdownMenu.Item>
                        {/each}
                    </DropdownMenu.Content>
                </DropdownMenu.Root>
                {#if $isAuthenticated}
                    <DropdownMenu.Root>
                        <DropdownMenu.Trigger class="hover:cursor-pointer" >
                            <UserAvatar />
                        </DropdownMenu.Trigger>
                        <DropdownMenu.Content class="w-28" align="end">
                            <DropdownMenu.Item class="hover:cursor-pointer" variant="destructive" onSelect={logout}>{$_('common.logout')}</DropdownMenu.Item>
                        </DropdownMenu.Content>
                    </DropdownMenu.Root>
                {/if}
            </div>
        </div>
        <main class="rounded-md py-4 flex flex-col gap-4">
            {@render children()}
        </main>
    </div>
{/if}
