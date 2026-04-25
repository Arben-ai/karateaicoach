<script>
  import { enhance } from "$app/forms";

  import { goto } from "$app/navigation";

  let { data, form } = $props();
  let isAdmin = $derived(data.isAdmin);
  let sportler = $derived(data.sportler);
  let pagination = $derived(data.pagination);
  let mySportler = $derived(data.mySportler);

  let searchInput = $state("");
  let guertelgradFilter = $state("");

  $effect(() => {
    searchInput = data.search ?? "";
    guertelgradFilter = data.guertelgrad ?? "";
  });

  const buildLink = (page, s = searchInput, g = guertelgradFilter) => {
    const p = new URLSearchParams({ page, size: pagination.size });
    if (s) p.set("search", s);
    if (g) p.set("guertelgrad", g);
    return `/sportler?${p}`;
  };

  const createPageLink = (page) => buildLink(page);

  function applyFilter() {
    goto(buildLink(0), { noScroll: true });
  }

  let deleteTarget = $state(null); // { id, name }

  function openDeleteModal(id, name) {
    deleteTarget = { id, name };
  }

  function closeDeleteModal() {
    deleteTarget = null;
  }

  const beltColor = (grad) => ({
    "Weiss":  { bg: "#f0f0f0", text: "#333",    border: "#ccc" },
    "Gelb":   { bg: "#FFD700", text: "#333",    border: "#e6c200" },
    "Orange": { bg: "#FF8C00", text: "#fff",    border: "#e07800" },
    "Grün":   { bg: "#28a745", text: "#fff",    border: "#1e7e34" },
    "Blau":   { bg: "#007bff", text: "#fff",    border: "#0062cc" },
    "Braun":  { bg: "#8B4513", text: "#fff",    border: "#6d3410" },
    "Schwarz":{ bg: "#555",    text: "#fff",    border: "#444" },
  }[grad] ?? { bg: "var(--card-bg)", text: "var(--text-muted)", border: "var(--border-color)" });
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
{#if form?.auth0Warning}
  <div class="alert alert-warning d-flex align-items-start gap-2" role="alert">
    <i class="bi bi-exclamation-triangle-fill flex-shrink-0 mt-1"></i>
    <div>
      <strong>Anmeldekonto noch aktiv.</strong>
      Der Sportler wurde aus der App entfernt, sein Auth0-Login-Konto existiert aber noch.
      Damit er sich nicht erneut mit derselben E-Mail registrieren kann, muss der Account auch im
      <a href="https://manage.auth0.com" target="_blank" rel="noopener" class="alert-link">Auth0 Dashboard</a>
      unter <strong>User Management → Users</strong> gelöscht werden.
    </div>
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

  <div class="row g-2 mb-3">
    <div class="col-sm-6 col-md-5">
      <div class="input-group">
        <span class="input-group-text" style="background: var(--card-bg); border-color: var(--border-color); color: var(--text-muted);">
          <i class="bi bi-search"></i>
        </span>
        <input
          class="form-control"
          type="text"
          placeholder="Nach Name suchen..."
          bind:value={searchInput}
          onkeydown={(e) => e.key === "Enter" && applyFilter()}
          style="background: var(--card-bg); border-color: var(--border-color); color: var(--text-primary);"
        />
      </div>
    </div>
    <div class="col-sm-4 col-md-3">
      <select
        class="form-select"
        bind:value={guertelgradFilter}
        onchange={applyFilter}
        style="background: var(--card-bg); border-color: var(--border-color); color: var(--text-primary);"
      >
        <option value="">Alle Gürtelgrade</option>
        <option value="Weiss">Weiss</option>
        <option value="Gelb">Gelb</option>
        <option value="Orange">Orange</option>
        <option value="Grün">Grün</option>
        <option value="Blau">Blau</option>
        <option value="Braun">Braun</option>
        <option value="Schwarz">Schwarz</option>
      </select>
    </div>
    <div class="col-sm-2 col-md-2">
      <button class="btn btn-primary w-100" onclick={applyFilter}>
        <i class="bi bi-search me-1"></i>Suchen
      </button>
    </div>
    {#if searchInput || guertelgradFilter}
      <div class="col-sm-2 col-md-2">
        <a href="/sportler" class="btn btn-outline-secondary w-100" data-sveltekit-noscroll>
          <i class="bi bi-x-circle me-1"></i>Reset
        </a>
      </div>
    {/if}
  </div>

  {#if sportler && sportler.length > 0}
    <div class="row row-cols-2 row-cols-md-3 row-cols-xl-4 g-3">
      {#each sportler as s}
        {@const belt = beltColor(s.guertelgrad)}
        <div class="col">
          <div class="sportler-card">
            {#if s.guertelgrad}
              <div class="sportler-card-accent" style="background:{belt.bg};"></div>
            {:else}
              <div class="sportler-card-accent" style="background: var(--border-color);"></div>
            {/if}
            <div class="sportler-card-top">
              <div class="sportler-avatar">
                {s.name.charAt(0).toUpperCase()}
              </div>
              <div class="sportler-name">{s.name}</div>
              <div class="sportler-email">{s.email}</div>
            </div>
            <div class="sportler-card-body">
              {#if s.guertelgrad}
                <span class="belt-badge" style="background:{belt.bg}; color:{belt.text}; border-color:{belt.border};">
                  <i class="bi bi-award me-1"></i>{s.guertelgrad}
                </span>
              {:else}
                <span class="belt-badge" style="background:var(--card-bg); color:var(--text-muted); border-color:var(--border-color);">
                  Kein Gürtelgrad
                </span>
              {/if}
              {#if s.gewicht}
                <span class="sportler-meta"><i class="bi bi-person-fill me-1"></i>{s.gewicht} kg</span>
              {/if}
            </div>
            <div class="sportler-card-footer">
              <button type="button" class="btn btn-outline-secondary btn-sm w-100"
                onclick={() => openDeleteModal(s.id, s.name)}>
                <i class="bi bi-trash me-1"></i>Löschen
              </button>
            </div>
          </div>
        </div>
      {/each}
    </div>
  {:else}
    <div class="card text-center py-5" style="color: var(--text-muted);">
      <i class="bi bi-people" style="font-size: 2.5rem; display: block; margin-bottom: 0.75rem;"></i>
      {#if data.search || data.guertelgrad}
        Keine Sportler für diese Suche gefunden. <a href="/sportler">Filter zurücksetzen</a>
      {:else}
        Noch keine Sportler vorhanden
      {/if}
    </div>
  {/if}

  {#if pagination && pagination.totalPages > 1}
    <div class="d-flex justify-content-between align-items-center mt-3">
      <span class="pagination-info">Seite {pagination.page + 1} von {pagination.totalPages}</span>
      <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary btn-sm"
          class:disabled={pagination.page === 0}
          href={pagination.page === 0 ? "#" : createPageLink(pagination.page - 1)}
          data-sveltekit-noscroll>
          <i class="bi bi-chevron-left me-1"></i>Zurück
        </a>
        <a class="btn btn-outline-secondary btn-sm"
          class:disabled={pagination.page >= pagination.totalPages - 1}
          href={pagination.page >= pagination.totalPages - 1 ? "#" : createPageLink(pagination.page + 1)}
          data-sveltekit-noscroll>
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
          <button type="button" class="btn btn-outline-secondary btn-sm"
            onclick={() => openDeleteModal(mySportler.id, mySportler.name)}>
            <i class="bi bi-trash me-1"></i>Profil löschen
          </button>
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

<!-- Delete Confirmation Modal -->
{#if deleteTarget}
  <div class="modal-overlay" role="presentation" onclick={closeDeleteModal} onkeydown={(e) => e.key === 'Escape' && closeDeleteModal()}>
    <div class="modal-box" role="dialog" aria-modal="true" tabindex="-1" onclick={(e) => e.stopPropagation()} onkeydown={(e) => e.stopPropagation()}>
      <div class="modal-icon-wrap">
        <i class="bi bi-exclamation-triangle-fill modal-icon"></i>
      </div>
      <h3 class="modal-title">Wirklich löschen?</h3>
      <p class="modal-body-text">
        <strong>{deleteTarget.name}</strong> wird dauerhaft entfernt.<br />
        Diese Aktion kann nicht rückgängig gemacht werden.
      </p>
      <div class="modal-actions">
        <button class="btn btn-outline-secondary" onclick={closeDeleteModal}>
          <i class="bi bi-x-lg me-1"></i>Abbrechen
        </button>
        <form method="POST" action="?/deleteSportler"
          use:enhance={() => async ({ update }) => { closeDeleteModal(); await update(); }}>
          <input type="hidden" name="id" value={deleteTarget.id} />
          <button type="submit" class="btn btn-danger">
            <i class="bi bi-trash me-1"></i>Löschen
          </button>
        </form>
      </div>
    </div>
  </div>
{/if}

<style>
  .sportler-card {
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 14px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    position: relative;
    box-shadow: 0 2px 8px rgba(0,0,0,0.25);
    transition: transform 0.15s, box-shadow 0.15s;
  }
  .sportler-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.4);
  }
  .sportler-card-accent {
    height: 6px;
    width: 100%;
    opacity: 0.85;
  }
  .sportler-card-top {
    padding: 1.25rem 1rem 0.75rem;
    text-align: center;
    border-bottom: 1px solid var(--border-color);
  }
  .sportler-avatar {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    background: var(--accent-soft);
    color: var(--accent);
    font-size: 1.4rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 0.6rem;
    border: 2px solid var(--accent);
  }
  .sportler-name {
    font-weight: 600;
    font-size: 0.95rem;
    color: var(--text-primary);
    margin-bottom: 0.2rem;
  }
  .sportler-email {
    font-size: 0.78rem;
    color: var(--text-muted);
    word-break: break-all;
  }
  .sportler-card-body {
    padding: 0.75rem 1rem;
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
    flex-grow: 1;
  }
  .belt-badge {
    display: inline-block;
    padding: 0.25rem 0.65rem;
    border-radius: 20px;
    font-size: 0.78rem;
    font-weight: 600;
    border: 1px solid;
    text-align: center;
  }
  .sportler-meta {
    font-size: 0.8rem;
    color: var(--text-muted);
  }
  .sportler-card-footer {
    padding: 0.75rem 1rem;
    border-top: 1px solid var(--border-color);
  }

  .modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(3px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    animation: fadeIn 0.15s ease;
  }
  .modal-box {
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 16px;
    padding: 2rem;
    max-width: 400px;
    width: 90%;
    text-align: center;
    animation: slideUp 0.2s ease;
  }
  .modal-icon-wrap {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: rgba(220, 53, 69, 0.12);
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1rem;
  }
  .modal-icon {
    font-size: 1.5rem;
    color: #dc3545;
  }
  .modal-title {
    font-size: 1.1rem;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 0.5rem;
  }
  .modal-body-text {
    font-size: 0.9rem;
    color: var(--text-muted);
    margin-bottom: 1.5rem;
    line-height: 1.6;
  }
  .modal-actions {
    display: flex;
    gap: 0.75rem;
    justify-content: center;
  }
  .modal-actions form {
    margin: 0;
  }
  @keyframes fadeIn {
    from { opacity: 0; }
    to   { opacity: 1; }
  }
  @keyframes slideUp {
    from { opacity: 0; transform: translateY(12px); }
    to   { opacity: 1; transform: translateY(0); }
  }
</style>
