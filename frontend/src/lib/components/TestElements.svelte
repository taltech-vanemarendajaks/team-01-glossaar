<script lang="ts">
    import { onMount } from 'svelte';

    let status = 'Checking...';
    let hello = 'Checking...';
    let user = 'Checking...';
    let error: string | null = null;

    type DbUser = {
        id: number;
        username: string;
        displayName: string;
    };

    let users: DbUser[] = [];
    let username = '';
    let displayName = '';
    let saving = false;
    let usersLoading = false;

    async function loadUsers() {
        usersLoading = true;
        try {
            const res = await fetch('/api/users');
            if (!res.ok) throw new Error(`Users HTTP ${res.status}`);
            users = await res.json();
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            usersLoading = false;
        }
    }

    async function addUser() {
        error = null;

        const u = username.trim();
        const d = displayName.trim();

        if (!u || !d) {
            error = 'Please fill in both username and display name.';
            return;
        }

        saving = true;
        try {
            const res = await fetch('/api/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: u, displayName: d })
            });

            if (!res.ok) {
                const text = await res.text().catch(() => '');
                throw new Error(text || `Create HTTP ${res.status}`);
            }

            const created = (await res.json().catch(() => null)) as DbUser | null;

            if (created) {
                users = [created, ...users];
            } else {
                await loadUsers();
            }

            username = '';
            displayName = '';
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
        } finally {
            saving = false;
        }
    }

    onMount(async () => {
        try {
            const res = await fetch('/api/health');
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            status = await res.text();
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
            status = 'Failed';
        }

        try {
            const res = await fetch('/api/hello');
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            hello = await res.text();
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
            hello = 'Failed';
        }

        try {
            const res = await fetch('/api/user');
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            user = await res.text();
        } catch (e) {
            error = e instanceof Error ? e.message : String(e);
            user = 'Failed';
        }

        await loadUsers();
    });
</script>

<div class="mx-auto w-full max-w-xl px-4 py-6 sm:py-10">
    <!-- Header -->
    <div class="mb-6">
        <h1 class="text-xl font-semibold tracking-tight sm:text-2xl">Backend status</h1>
        <p class="mt-1 text-sm text-zinc-600">
            Health check + simple endpoints + users stored in Postgres.
        </p>
    </div>

    <!-- Error -->
    {#if error}
        <div class="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            <div class="font-medium">Error</div>
            <div class="mt-1 break-words">{error}</div>
        </div>
    {/if}

    <!-- Status cards -->
    <div class="grid gap-3 sm:grid-cols-3">
        <div class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
            <div class="text-xs font-medium uppercase tracking-wide text-zinc-500">Health</div>
            <div class="mt-2 text-sm font-semibold text-zinc-900">{status}</div>
        </div>

        <div class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
            <div class="text-xs font-medium uppercase tracking-wide text-zinc-500">Hello</div>
            <div class="mt-2 text-sm font-semibold text-zinc-900">{hello}</div>
        </div>

        <div class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm">
            <div class="text-xs font-medium uppercase tracking-wide text-zinc-500">User</div>
            <div class="mt-2 text-sm font-semibold text-zinc-900">{user}</div>
        </div>
    </div>

    <div class="my-6 h-px w-full bg-zinc-200"></div>

    <!-- Add user -->
    <div class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm sm:p-6">
        <div class="mb-4">
            <h2 class="text-lg font-semibold tracking-tight">Add user</h2>
            <p class="mt-1 text-sm text-zinc-600">Create a new user and refresh the list.</p>
        </div>

        <form on:submit|preventDefault={addUser} class="grid gap-4">
            <div class="grid gap-1.5">
                <label for="username" class="text-sm font-medium text-zinc-900">Username</label>
                <input
                        id="username"
                        bind:value={username}
                        placeholder="robot1"
                        autocomplete="off"
                        class="h-11 w-full rounded-xl border border-zinc-300 bg-white px-3 text-sm text-zinc-900 shadow-sm outline-none transition focus:border-zinc-900 focus:ring-2 focus:ring-zinc-900/10"
                />
            </div>

            <div class="grid gap-1.5">
                <label for="displayName" class="text-sm font-medium text-zinc-900">Display name</label>
                <input
                        id="displayName"
                        bind:value={displayName}
                        placeholder="I am Robot"
                        autocomplete="off"
                        class="h-11 w-full rounded-xl border border-zinc-300 bg-white px-3 text-sm text-zinc-900 shadow-sm outline-none transition focus:border-zinc-900 focus:ring-2 focus:ring-zinc-900/10"
                />
            </div>

            <button
                    type="submit"
                    disabled={saving}
                    class="inline-flex h-11 items-center justify-center rounded-xl bg-zinc-900 px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-zinc-800 disabled:cursor-not-allowed disabled:opacity-60"
            >
                {#if saving}
          <span class="inline-flex items-center gap-2">
            <span class="h-4 w-4 animate-spin rounded-full border-2 border-white/30 border-t-white"></span>
            Saving...
          </span>
                {:else}
                    Add user
                {/if}
            </button>
        </form>
    </div>

    <!-- Users -->
    <div class="mt-6 rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm sm:p-6">
        <div class="mb-4 flex items-center justify-between gap-3">
            <div>
                <h2 class="text-lg font-semibold tracking-tight">Users</h2>
                <p class="mt-1 text-sm text-zinc-600">
                    {usersLoading ? 'Loading...' : `${users.length} total`}
                </p>
            </div>

            <button
                    type="button"
                    on:click={loadUsers}
                    disabled={usersLoading}
                    class="inline-flex h-10 items-center justify-center rounded-xl border border-zinc-300 bg-white px-3 text-sm font-medium text-zinc-900 shadow-sm transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-60"
            >
                Refresh
            </button>
        </div>

        {#if usersLoading}
            <div class="flex items-center gap-3 rounded-xl border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm text-zinc-700">
                <span class="h-4 w-4 animate-spin rounded-full border-2 border-zinc-400/30 border-t-zinc-700"></span>
                Loading users...
            </div>
        {:else if users.length === 0}
            <div class="rounded-xl border border-dashed border-zinc-300 bg-zinc-50 px-4 py-6 text-center text-sm text-zinc-600">
                No users yet. Add one above.
            </div>
        {:else}

            <ul class="divide-y divide-zinc-200 overflow-hidden rounded-xl border border-zinc-200">
                {#each users as u (u.id)}
                    <li class="flex items-start justify-between gap-3 px-4 py-3">
                        <div class="min-w-0">
                            <div class="truncate text-sm font-semibold text-zinc-900">{u.username}</div>
                            <div class="truncate text-sm text-zinc-600">{u.displayName}</div>
                        </div>
                        <span class="shrink-0 rounded-full bg-zinc-100 px-2.5 py-1 text-xs font-medium text-zinc-700">
              #{u.id}
            </span>
                    </li>
                {/each}
            </ul>
        {/if}
    </div>
</div>