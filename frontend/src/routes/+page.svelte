<script>
  import Button from '$lib/components/ui/button/button.svelte';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
  import * as Tabs from '$lib/components/ui/tabs/index.js';
  import Input from '$lib/components/ui/input/input.svelte';
  import Textarea from '$lib/components/ui/textarea/textarea.svelte';
</script>

<div>

  <h2>using raw tailwind</h2>
  <a
          class="group inline-block rounded-full bg-linear-to-r from-pink-500 via-red-500 to-yellow-500 p-0.5 hover:text-white"
          href="#"
  >
    <span class="block rounded-full bg-white px-8 py-3 text-sm font-medium group-hover:bg-transparent">
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
    <Textarea />
  </div>

  <div>
    Input:
    <Input />
  </div>

  <div>
    Dropdown Menu:
    <br />
    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild>
        <Button variant="outline">Locale</Button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Content>
        <DropdownMenu.Group>
          <DropdownMenu.Label>et</DropdownMenu.Label>
          <DropdownMenu.Item>en</DropdownMenu.Item>
        </DropdownMenu.Group>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </div>

  <div>
    Tabs:
    <Tabs.Root value="add" class="w-[400px]">
      <Tabs.List>
        <Tabs.Trigger value="add">Add</Tabs.Trigger>
        <Tabs.Trigger value="list">List</Tabs.Trigger>
        <Tabs.Trigger value="quiz">Quiz</Tabs.Trigger>
      </Tabs.List>

      <Tabs.Content value="add">Add a word</Tabs.Content>
      <Tabs.Content value="list">List words</Tabs.Content>
      <Tabs.Content value="quiz">Take a quiz</Tabs.Content>
    </Tabs.Root>
  </div>
</div>

<style lang="scss">
  h2 {
    margin-top: 3rem;
  }
  div {
    margin-bottom: 1rem;
  }
</style>