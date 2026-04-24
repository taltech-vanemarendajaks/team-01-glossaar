<script lang="ts">
    import * as NavigationMenu from '$lib/components/ui/navigation-menu';
    import Button from '$lib/components/ui/button/button.svelte';
    import { page } from '$app/stores'; // TODO: deprecated
    import { isAuthenticated } from '$lib/stores/auth';
    import { _ } from 'svelte-i18n';

    const routes = [
        { key: 'login', href: '/login', authenticated: false },
        { key: 'add', href: '/add', authenticated: true },
        { key: 'list', href: '/list', authenticated: true },
        { key: 'quiz', href: '/quiz', authenticated: true },
    ];

    type ListItemProps = {
        title: string;
        href: string;
        active: boolean;
    };
</script>

{#snippet ListItem({ title, href, active }: ListItemProps)}
    <NavigationMenu.Item>
        <NavigationMenu.Link>
            {#snippet child()}
                <Button {href} variant="ghost" size="sm" className={`border box-border border-transparent ${active ? 'border-zinc' : ''}`}>
                    {title}
                </Button>
            {/snippet}
        </NavigationMenu.Link>
    </NavigationMenu.Item>
{/snippet}

<NavigationMenu.Root>
    <NavigationMenu.List class="flex w-full p-1">
        {#each routes.filter((item) => !('authenticated' in item) || item.authenticated === $isAuthenticated) as item (item.href)}
            {@render ListItem({
                title: $_(`nav.${item.key}`),
                href: item.href,
                active: $page.url.pathname === item.href
            })}
        {/each}
    </NavigationMenu.List>
</NavigationMenu.Root>
