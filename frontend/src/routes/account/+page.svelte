<script>
  let { data } = $props();
  let user = $derived(data.user);
  let isAuthenticated = $derived(data.isAuthenticated);
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-person-circle me-2"></i>Account</h1>
</div>
<p class="page-subtitle">Deine Profilinformationen</p>

{#if isAuthenticated}
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="profile-card">
        <div class="profile-header">
          {#if user.picture}
            <img src={user.picture} alt="Profilbild" class="profile-avatar" />
          {:else}
            <div class="profile-avatar d-flex align-items-center justify-content-center" style="background: var(--accent-soft); color: var(--accent); font-size: 2rem; font-weight: 700; margin: 0 auto 1rem;">
              {user.name?.charAt(0).toUpperCase()}
            </div>
          {/if}
          <div class="profile-name">{user.name}</div>
          {#if user.user_roles && user.user_roles.length > 0}
            {#each user.user_roles as role}
              <span class="profile-role-badge">
                <i class="bi bi-shield-check me-1"></i>{role}
              </span>
            {/each}
          {/if}
        </div>

        <div class="profile-body">
          {#if user.given_name}
            <div class="profile-info-row">
              <span class="profile-info-label">Vorname</span>
              <span class="profile-info-value">{user.given_name}</span>
            </div>
          {/if}
          {#if user.family_name}
            <div class="profile-info-row">
              <span class="profile-info-label">Nachname</span>
              <span class="profile-info-value">{user.family_name}</span>
            </div>
          {/if}
          <div class="profile-info-row">
            <span class="profile-info-label">E-Mail</span>
            <span class="profile-info-value">{user.email}</span>
          </div>
          {#if user.nickname}
            <div class="profile-info-row">
              <span class="profile-info-label">Benutzername</span>
              <span class="profile-info-value">@{user.nickname}</span>
            </div>
          {/if}
        </div>
      </div>
    </div>
  </div>
{:else}
  <div class="text-center py-5" style="color: var(--text-muted);">
    <i class="bi bi-lock" style="font-size: 2.5rem; display: block; margin-bottom: 1rem;"></i>
    <p>Du bist nicht eingeloggt.</p>
    <a href="/login" class="btn btn-primary">Zum Login</a>
  </div>
{/if}
