<script lang="ts">
  import './layout.css';
  import favicon from '$lib/assets/favicon.svg';
  import * as NavigationMenu from '$lib/components/ui/navigation-menu';
  import { cn } from '$lib/utils';
  import type { HTMLAttributes } from 'svelte/elements';
  import { page } from '$app/stores'; // TODO: deprecated

  let { children } = $props();

  const navigationItems = [
    { name: 'Index', href: '/' },
    { name: 'Add', href: '/add' },
    { name: 'List', href: '/list' },
    { name: 'Quiz', href: '/quiz' },
  ];

  type ListItemProps = HTMLAttributes<HTMLAnchorElement> & {
    title: string;
    href: string;
  };
</script>

<svelte:head>
  <link rel="icon" href={favicon} />
</svelte:head>

{#snippet ListItem({ title, href, class: className }: ListItemProps)}
  <NavigationMenu.Item>
    <NavigationMenu.Link>
      {#snippet child()}
        <a
          {href}
          class={cn(
            'box-border block rounded-md p-2 leading-none no-underline hover:bg-accent hover:text-accent-foreground active:border-primary',
            className
          )}
        >
          <span class="text-sm leading-none font-medium">{title}</span>
        </a>
      {/snippet}
    </NavigationMenu.Link>
  </NavigationMenu.Item>
{/snippet}

<div class="mx-auto my-8 h-full w-full max-w-[24.5rem] gap-2 rounded-md border p-1">
  <NavigationMenu.Root>
    <NavigationMenu.List class="flex w-full rounded-md border p-1">
      {#each navigationItems as item}
        {@render ListItem({
          title: item.name,
          href: item.href,
          class: $page.url.pathname === item.href ? 'border' : ''
        })}
      {/each}
    </NavigationMenu.List>
  </NavigationMenu.Root>

  <main class="mt-2 rounded-md border p-2">
    {@render children()}
  </main>
</div>
