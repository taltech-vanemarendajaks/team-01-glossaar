<script>
    import Button from "$lib/components/ui/button/button.svelte";
    import Textarea from "$lib/components/ui/textarea/textarea.svelte";
</script>

<style lang="scss">
    main {
        padding: 2rem;
    }
    h2 {
        margin-top: 3rem;
    }
    div {
        margin-bottom: 1rem;
    }
</style>

<h1>Welcome to SvelteKit</h1>
<p>Visit <a href="https://svelte.dev/docs/kit">svelte.dev/docs/kit</a> to read the documentation</p>


<h2> using raw tailwind</h2>
<a
class="group inline-block rounded-full bg-linear-to-r from-pink-500 via-red-500 to-yellow-500 p-0.5 hover:text-white"
href="#"
>
<span
    class="block rounded-full bg-white px-8 py-3 text-sm font-medium group-hover:bg-transparent"
>
    raw tailwind btn
</span>
</a>




<h2>Using shadcn components</h2>
<div>
    Button:
    <Button variant="outline" size="lg">sveltekit button</Button>
</div>
<div>
    Textarea:
    <Textarea/>
</div>