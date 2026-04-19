<script lang="ts">
    import * as NavigationMenu from '$lib/components/ui/navigation-menu';
    import { cn } from '$lib/utils';
    import type { HTMLAttributes } from 'svelte/elements';
    import { page } from '$app/stores'; // TODO: deprecated
    import { isAuthenticated } from '$lib/stores/auth';

    const routes = [
        { name: 'Login', href: '/login', authenticated: false },
        { name: 'Add', href: '/add', authenticated: true },
        { name: 'List', href: '/list', authenticated: true },
        { name: 'Quiz', href: '/quiz', authenticated: true },
    ];

    type ListItemProps = HTMLAttributes<HTMLAnchorElement> & {
        title: string;
        href: string;
    };
</script>

{#snippet ListItem({ title, href, class: className }: ListItemProps)}
    <NavigationMenu.Item>
        <NavigationMenu.Link>
            {#snippet child()}
                <a
                    {href}
                    class={cn(
                        'box-border block rounded-md p-2 leading-none no-underline hover:bg-accent hover:text-accent-foreground active:border-primary border border-transparent',
                        className
                    )}
                >
                    <span class="text-sm leading-none font-medium">{title}</span>
                </a>
            {/snippet}
        </NavigationMenu.Link>
    </NavigationMenu.Item>
{/snippet}

<NavigationMenu.Root>
    <NavigationMenu.List class="flex w-full p-1">
        {#each routes.filter((item) => !('authenticated' in item) || item.authenticated === $isAuthenticated) as item (item.href)}
            {@render ListItem({
                title: item.name,
                href: item.href,
                class: $page.url.pathname === item.href ? 'border' : ''
            })}
        {/each}
    </NavigationMenu.List>
</NavigationMenu.Root>
