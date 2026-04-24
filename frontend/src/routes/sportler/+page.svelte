<script>
  import { enhance } from "$app/forms";

  let { data, form } = $props();
  let isAdmin = $derived(data.isAdmin);
  let sportler = $derived(data.sportler);
  let pagination = $derived(data.pagination);
  let mySportler = $derived(data.mySportler);

  const createPageLink = (page) => `/sportler?page=${page}&size=${pagination.size}`;
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-people-fill me-2"></i>Sportler</h1>
</div>
<p class="page-subtitle">
  {#if isAdmin}Alle registrierten Athleten verwalten{:else}Dein Sportlerprofil{/if}
</p>

{#if form?.success}
  <div class="alert alert-success" role="alert">
    <i class="bi bi-check-circle me-2"></i>
    {form.deleted ? "Profil erfolgreich gelöscht." : "Profil erfolgreich erstellt!"}
  </div>
{/if}
{#if form?.error}
  <div class="alert alert-danger" role="alert">
    <i class="bi bi-exclamation-circle me-2"></i>{form.error}
  </div>
{/if}

<!-- ADMIN ANSICHT -->
{#if isAdmin}
  <div class="card mb-4">
    <div class="card-header">
      <i class="bi bi-person-plus me-2"></i>Neuen Sportler erfassen
    </div>
    <div class="card-body p-4">
      <form method="POST" action="?/createSportler" use:enhance>
        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <label class="form-label" for="name">Name</label>
            <input class="form-control" id="name" name="name" type="text" placeholder="Vorname Nachname" required />
          </div>
          <div class="col-md-6">
            <label class="form-label" for="email">E-Mail</label>
            <input class="form-control" id="email" name="email" type="email" placeholder="name@beispiel.ch" required />
          </div>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-md-8">
            <label class="form-label" for="guertelgrad">Gürtelgrad</label>
            <select class="form-select" id="guertelgrad" name="guertelgrad">
              <option value="">Kein Gürtelgrad</option>
              <option value="Weiss">Weiss</option>
              <option value="Gelb">Gelb</option>
              <option value="Orange">Orange</option>
              <option value="Grün">Grün</option>
              <option value="Blau">Blau</option>
              <option value="Braun">Braun</option>
              <option value="Schwarz">Schwarz</option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label" for="gewicht">Gewicht (kg)</label>
            <input class="form-control" id="gewicht" name="gewicht" type="number" placeholder="70" min="0" />
          </div>
        </div>
        <button type="submit" class="btn btn-primary">
          <i class="bi bi-plus-circle me-2"></i>Sportler hinzufügen
        </button>
      </form>
    </div>
  </div>

  <hr class="section-divider" />

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="h5 mb-0" style="color: var(--text-muted);">
      <i class="bi bi-list-ul me-2"></i>Übersicht
    </h2>
    {#if pagination}
      <span class="pagination-info">{pagination.totalElements} Sportler total</span>
    {/if}
  </div>

  <div class="card">
    <div class="table-responsive">
      <table class="table mb-0">
        <thead>
          <tr>
            <th>Name</th>
            <th>E-Mail</th>
            <th>ID</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {#if sportler && sportler.length > 0}
            {#each sportler as s}
              <tr>
                <td>
                  <div class="d-flex align-items-center gap-2">
                    <div style="width: 32px; height: 32px; border-radius: 50%; background: var(--accent-soft); display: flex; align-items: center; justify-content: center; color: var(--accent); font-size: 0.85rem; font-weight: 700; flex-shrink: 0;">
                      {s.name.charAt(0).toUpperCase()}
                    </div>
                    <span>{s.name}</span>
                  </div>
                </td>
                <td style="color: var(--text-muted);">{s.email}</td>
                <td><code style="color: var(--text-muted); font-size: 0.75rem;">{s.id}</code></td>
                <td>
                  <form method="POST" action="?/deleteSportler" use:enhance>
                    <input type="hidden" name="id" value={s.id} />
                    <button type="submit" class="btn btn-outline-secondary btn-sm" aria-label="Löschen"
                      onclick={() => confirm('Sportler wirklich löschen?')}>
                      <i class="bi bi-trash"></i>
                    </button>
                  </form>
                </td>
              </tr>
            {/each}
          {:else}
            <tr>
              <td colspan="4" class="text-center py-5" style="color: var(--text-muted);">
                <i class="bi bi-people" style="font-size: 2rem; display: block; margin-bottom: 0.5rem;"></i>
                Noch keine Sportler vorhanden
              </td>
            </tr>
          {/if}
        </tbody>
      </table>
    </div>
  </div>

  {#if pagination && pagination.totalPages > 1}
    <div class="d-flex justify-content-between align-items-center mt-3">
      <span class="pagination-info">Seite {pagination.page + 1} von {pagination.totalPages}</span>
      <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary btn-sm"
          class:disabled={pagination.page === 0}
          href={pagination.page === 0 ? "#" : createPageLink(pagination.page - 1)}>
          <i class="bi bi-chevron-left me-1"></i>Zurück
        </a>
        <a class="btn btn-outline-secondary btn-sm"
          class:disabled={pagination.page >= pagination.totalPages - 1}
          href={pagination.page >= pagination.totalPages - 1 ? "#" : createPageLink(pagination.page + 1)}>
          Weiter<i class="bi bi-chevron-right ms-1"></i>
        </a>
      </div>
    </div>
  {/if}

<!-- SPORTLER ANSICHT -->
{:else}
  {#if mySportler}
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="profile-card">
          <div class="profile-header">
            <div style="width: 70px; height: 70px; border-radius: 50%; background: var(--accent-soft); border: 3px solid var(--accent); display: flex; align-items: center; justify-content: center; color: var(--accent); font-size: 1.75rem; font-weight: 700; margin: 0 auto 1rem;">
              {mySportler.name.charAt(0).toUpperCase()}
            </div>
            <div class="profile-name">{mySportler.name}</div>
            <span class="profile-role-badge"><i class="bi bi-person me-1"></i>Sportler</span>
          </div>
          <div class="profile-body">
            <div class="profile-info-row">
              <span class="profile-info-label">E-Mail</span>
              <span class="profile-info-value">{mySportler.email}</span>
            </div>
            {#if mySportler.guertelgrad}
              <div class="profile-info-row">
                <span class="profile-info-label">Gürtelgrad</span>
                <span class="profile-info-value">{mySportler.guertelgrad}</span>
              </div>
            {/if}
            {#if mySportler.gewicht}
              <div class="profile-info-row">
                <span class="profile-info-label">Gewicht</span>
                <span class="profile-info-value">{mySportler.gewicht} kg</span>
              </div>
            {/if}
            <div class="profile-info-row">
              <span class="profile-info-label">ID</span>
              <code style="color: var(--text-muted); font-size: 0.75rem;">{mySportler.id}</code>
            </div>
          </div>
        </div>

        <div class="mt-3 text-center">
          <form method="POST" action="?/deleteSportler" use:enhance>
            <input type="hidden" name="id" value={mySportler.id} />
            <button type="submit" class="btn btn-outline-secondary btn-sm"
              onclick={() => confirm('Profil wirklich löschen?')}>
              <i class="bi bi-trash me-1"></i>Profil löschen
            </button>
          </form>
        </div>
      </div>
    </div>

  {:else}
    <div class="card">
      <div class="card-header">
        <i class="bi bi-person-plus me-2"></i>Sportlerprofil erstellen
      </div>
      <div class="card-body p-4">
        <p style="color: var(--text-muted);" class="mb-4">Du hast noch kein Sportlerprofil. Erstelle jetzt deines.</p>
        <form method="POST" action="?/createSportler" use:enhance>
          <div class="row g-3 mb-3">
            <div class="col-md-6">
              <label class="form-label" for="name">Name</label>
              <input class="form-control" id="name" name="name" type="text" placeholder="Vorname Nachname" required />
            </div>
            <div class="col-md-6">
              <label class="form-label" for="email">E-Mail</label>
              <input class="form-control" id="email" name="email" type="email" placeholder="name@beispiel.ch" required />
            </div>
          </div>
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-plus-circle me-2"></i>Profil erstellen
          </button>
        </form>
      </div>
    </div>
  {/if}
{/if}
