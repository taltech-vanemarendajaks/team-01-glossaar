<script lang="ts">
    import { onMount } from 'svelte';

    let status = 'Checking...';
    let hello = 'Checking...';
    let user = 'Checking...';
    let error: string | null = null;

    // --- DB-backed users ---
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

            // If backend returns the created user JSON:
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
        // keep your existing calls
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

        // load DB users list
        await loadUsers();
    });
</script>

<h1>Health Check</h1>

{#if error}
    <p style="color:red">Error: {error}</p>
{/if}

<p>Backend says: <strong>{status}</strong></p>
<p>Backend says: <strong>{hello}</strong></p>
<p>Backend says: <strong>{user}</strong></p>

<hr />

<h2>Add User</h2>

<form on:submit|preventDefault={addUser} style="max-width: 420px; display: grid; gap: 10px;">
    <label>
        Username
        <input
                bind:value={username}
                placeholder="robot1"
                autocomplete="off"
                style="width: 100%; padding: 8px;"
        />
    </label>

    <label>
        Display name
        <input
                bind:value={displayName}
                placeholder="I am Robot"
                autocomplete="off"
                style="width: 100%; padding: 8px;"
        />
    </label>

    <button type="submit" disabled={saving} style="padding: 10px;">
        {saving ? 'Saving...' : 'Add User'}
    </button>
</form>

<h2 style="margin-top: 18px;">Users</h2>

{#if usersLoading}
    <p>Loading users...</p>
{:else if users.length === 0}
    <p>No users yet.</p>
{:else}
    <ul style="padding-left: 18px;">
        {#each users as u (u.id)}
            <li>
                <strong>{u.username}</strong> — {u.displayName} <span style="color:#666">(id: {u.id})</span>
            </li>
        {/each}
    </ul>
{/if}