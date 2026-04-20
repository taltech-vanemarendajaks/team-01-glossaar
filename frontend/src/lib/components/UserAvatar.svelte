<script lang="ts">
    import { user } from '$lib/stores/auth';
    import * as Avatar from '$lib/components/ui/avatar/index';
</script>

<Avatar.Root>
    <Avatar.Image src={$user?.avatarUrl} alt="User Avatar" />
    <Avatar.Badge class="bg-white">
        <img src={`/${$user?.authProvider?.toLowerCase()}-logo.svg`} alt="user-auth-provider">
    </Avatar.Badge>
</Avatar.Root>