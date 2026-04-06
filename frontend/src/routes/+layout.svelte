<script lang="ts">
  import './layout.css';
  import favicon from '$lib/assets/favicon.svg';
  import Navigation from '$lib/components/Navigation.svelte';

  let { children } = $props();
</script>

<svelte:head>
  <link rel="icon" href={favicon} />
</svelte:head>

<div class="mx-auto my-8 h-full w-full max-w-[24.5rem] gap-2 rounded-md border p-1">
  <Navigation />

  <main class="mt-2 rounded-md border p-2">
    {@render children()}
  </main>
</div>
