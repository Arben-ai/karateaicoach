<script>
  import { enhance } from '$app/forms';

  let { data, form } = $props();
  let user = $derived(data.user);
  let isAuthenticated = $derived(data.isAuthenticated);
  let sportler = $derived(data.sportler);

  let isAdmin = $derived(
    Array.isArray(user?.user_roles) &&
    user.user_roles.some(r => r.toLowerCase() === 'admin')
  );
  let profileIncomplete = $derived(
    !isAdmin && sportler && (!sportler.guertelgrad || !sportler.gewicht)
  );
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-person-circle me-2"></i>Account</h1>
</div>
<p class="page-subtitle">Deine Profilinformationen</p>

{#if form?.success}
  <div class="alert alert-success" role="alert">
    <i class="bi bi-check-circle me-2"></i>Profil erfolgreich aktualisiert!
  </div>
{/if}
{#if form?.error}
  <div class="alert alert-danger" role="alert">
    <i class="bi bi-exclamation-circle me-2"></i>{form.error}
  </div>
{/if}

{#if profileIncomplete}
  <div class="alert alert-warning d-flex align-items-start gap-3 mb-4" role="alert">
    <i class="bi bi-exclamation-triangle-fill fs-5 mt-1"></i>
    <div class="flex-grow-1">
      <strong>Profil unvollständig</strong> — Bitte ergänze deinen Gürtelgrad und dein Gewicht.
      <form method="POST" action="?/updateProfile" use:enhance class="mt-3">
        <div class="row g-2">
          <div class="col-sm-5">
            <select class="form-select form-select-sm" name="guertelgrad">
              <option value="" selected={!sportler.guertelgrad}>Gürtelgrad wählen...</option>
              <option value="Weiss"  selected={sportler.guertelgrad === 'Weiss'}>Weiss</option>
              <option value="Gelb"   selected={sportler.guertelgrad === 'Gelb'}>Gelb</option>
              <option value="Orange" selected={sportler.guertelgrad === 'Orange'}>Orange</option>
              <option value="Grün"   selected={sportler.guertelgrad === 'Grün'}>Grün</option>
              <option value="Blau"   selected={sportler.guertelgrad === 'Blau'}>Blau</option>
              <option value="Braun"  selected={sportler.guertelgrad === 'Braun'}>Braun</option>
              <option value="Schwarz" selected={sportler.guertelgrad === 'Schwarz'}>Schwarz</option>
            </select>
          </div>
          <div class="col-sm-3">
            <input class="form-control form-control-sm" type="number" name="gewicht"
              placeholder="Gewicht (kg)" min="0" value={sportler.gewicht || ''} />
          </div>
          <div class="col-sm-3">
            <button type="submit" class="btn btn-warning btn-sm w-100">
              <i class="bi bi-check-lg me-1"></i>Speichern
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
{/if}

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
          {#if sportler}
            <div class="profile-info-row">
              <span class="profile-info-label">Gürtelgrad</span>
              <span class="profile-info-value">{sportler.guertelgrad || '—'}</span>
            </div>
            <div class="profile-info-row">
              <span class="profile-info-label">Gewicht</span>
              <span class="profile-info-value">{sportler.gewicht ? sportler.gewicht + ' kg' : '—'}</span>
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
