<script>
    let { data } = $props();
    let { user, isAuthenticated } = data;
</script>

<h1 class="mt-3">Account Details</h1>

{#if isAuthenticated}
    <div class="card p-3" style="max-width: 500px;">
        <p><img src={user.picture} alt="" width="100" /></p>
        <p><b>Name:</b> {user.name}</p>
        <p><b>Nickname:</b> {user.nickname}</p>
        <p><b>First Name:</b> {user.given_name}</p>
        <p><b>Last Name:</b> {user.family_name}</p>
        <p><b>Email:</b> {user.email}</p>

        {#if user.user_roles && user.user_roles.length > 0}
            <p><b>Roles:</b> {user.user_roles.join(", ")}</p>
        {/if}
    </div>
{:else}
    <p>Not logged in</p>
{/if}
