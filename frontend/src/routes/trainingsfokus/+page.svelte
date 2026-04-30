<script>
  import { enhance } from "$app/forms";
  import { goto } from "$app/navigation";

  let { data, form } = $props();
  let trainingsfokusse = $derived(data.trainingsfokus);
  let sportler = $derived(data.sportler);
  let pagination = $derived(data.pagination);

  let searchInput = $state("");
  let kategorieFilter = $state("");

  $effect(() => {
    searchInput = data.sportlerName ?? "";
    kategorieFilter = data.kategorie ?? "";
  });

  const buildLink = (page, s = searchInput, k = kategorieFilter) => {
    const p = new URLSearchParams({ page, size: pagination.size });
    if (s) p.set("sportlerName", s);
    if (k) p.set("kategorie", k);
    return `/trainingsfokus?${p}`;
  };

  const createPageLink = (page) => buildLink(page);

  function applyFilter() {
    goto(buildLink(0), { noScroll: true });
  }

  const statusClass = (status) =>
    status === "AKTIV" ? "badge-status-active" : "badge-status-archived";

  const statusLabel = (status) =>
    status === "AKTIV" ? "Aktiv" : "Inaktiv";

  let schwerpunktSelected = $state("");
</script>

<!-- ── SEKTION 1: Titel + Formular ── -->
<section class="tf-form-section">
  <div class="page-content">
    <div class="d-flex justify-content-between align-items-center mb-1">
      <h1 class="page-title"><i class="bi bi-bullseye me-2"></i>Trainingsfokus</h1>
    </div>
    <p class="page-subtitle">Trainingsschwerpunkte für Sportler definieren</p>

    <div class="tf-form-card">
      <div class="tf-form-card-header">
        <i class="bi bi-plus-circle me-2"></i>Neuen Trainingsfokus erfassen
      </div>
      <div class="tf-form-card-body">
        {#if form?.success}
          <div class="alert alert-success" role="alert">
            <i class="bi bi-check-circle me-2"></i>Trainingsfokus erfolgreich erstellt!
          </div>
        {/if}
        {#if form?.error}
          <div class="alert alert-danger" role="alert">
            <i class="bi bi-exclamation-circle me-2"></i>{form.error}
          </div>
        {/if}

        <form method="POST" action="?/createTrainingsfokus" use:enhance>
          <div class="row g-3 mb-3">
            <div class="col-md-4">
              <label class="form-label" for="kategorie">Kategorie</label>
              <select class="form-select" id="kategorie" name="kategorie" required>
                <option value="">Kategorie wählen...</option>
                <option value="Kata">Kata</option>
                <option value="Kumite">Kumite</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label" for="schwerpunkt">Schwerpunkt</label>
              <select class="form-select" id="schwerpunkt" name="schwerpunkt"
                bind:value={schwerpunktSelected} required>
                <option value="">Schwerpunkt wählen...</option>
                <option value="Ausdauer">Ausdauer</option>
                <option value="Beweglichkeit">Beweglichkeit</option>
                <option value="Gleichgewicht">Gleichgewicht</option>
                <option value="Grundtechnik">Grundtechnik</option>
                <option value="Kondition">Kondition</option>
                <option value="Koordination">Koordination</option>
                <option value="Kraft">Kraft</option>
                <option value="Mentale Stärke">Mentale Stärke</option>
                <option value="Partnerübungen">Partnerübungen</option>
                <option value="Schnelligkeit">Schnelligkeit</option>
                <option value="Selbstverteidigung">Selbstverteidigung</option>
                <option value="Taktik">Taktik</option>
                <option value="Wettkampfvorbereitung">Wettkampfvorbereitung</option>
                <option value="Andere">Andere</option>
              </select>
              {#if schwerpunktSelected === "Andere"}
                <input
                  class="form-control mt-2"
                  type="text"
                  name="schwerpunktAndere"
                  placeholder="Schwerpunkt beschreiben..."
                  maxlength="50"
                  required
                />
                <div class="form-text">Maximal 50 Zeichen</div>
              {/if}
            </div>
            <div class="col-md-4">
              <label class="form-label" for="sportlerId">Sportler</label>
              <select class="form-select" id="sportlerId" name="sportlerId" required>
                <option value="">Sportler wählen...</option>
                {#each sportler as s}
                  <option value={s.id}>{s.name}</option>
                {/each}
              </select>
            </div>
          </div>
          <div class="row g-3 mb-3">
            <div class="col-12">
              <label class="form-label" for="notiz">Notiz für den Sportler</label>
              <textarea
                class="form-control"
                id="notiz"
                name="notiz"
                rows="3"
                maxlength="300"
                placeholder="Was soll der Sportler konkret üben? Z.B. «Fokus auf saubere Hüftrotation bei Gedan Barai, Tempo bewusst rausnehmen.»"
              ></textarea>
              <div class="form-text">Optional · maximal 300 Zeichen</div>
            </div>
          </div>
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-plus-circle me-2"></i>Fokus hinzufügen
          </button>
        </form>
      </div>
    </div>
  </div>
</section>

<!-- ── SEKTION 2: Übersicht ── -->
<section class="tf-list-section">
  <div class="page-content">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="h5 mb-0" style="color: var(--text-muted);">
        <i class="bi bi-list-ul me-2"></i>Übersicht
      </h2>
      {#if pagination}
        <span class="pagination-info">{pagination.totalElements} Einträge total</span>
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
            placeholder="Sportler suchen..."
            bind:value={searchInput}
            onkeydown={(e) => e.key === "Enter" && applyFilter()}
            style="background: var(--card-bg); border-color: var(--border-color); color: var(--text-primary);"
          />
        </div>
      </div>
      <div class="col-sm-3 col-md-2">
        <select
          class="form-select"
          bind:value={kategorieFilter}
          onchange={applyFilter}
          style="background: var(--card-bg); border-color: var(--border-color); color: var(--text-primary);"
        >
          <option value="">Alle Kategorien</option>
          <option value="Kata">Kata</option>
          <option value="Kumite">Kumite</option>
        </select>
      </div>
      <div class="col-sm-2 col-md-2">
        <button class="btn btn-primary w-100" onclick={applyFilter}>
          <i class="bi bi-search me-1"></i>Suchen
        </button>
      </div>
      {#if searchInput || kategorieFilter}
        <div class="col-sm-2 col-md-2">
          <a href="/trainingsfokus" class="btn btn-outline-secondary w-100" data-sveltekit-noscroll>
            <i class="bi bi-x-circle me-1"></i>Reset
          </a>
        </div>
      {/if}
    </div>

    {#if trainingsfokusse && trainingsfokusse.length > 0}
      <div class="fokus-list">
        {#each trainingsfokusse as fokus}
          {@const sportlerName = sportler.find(s => s.id === fokus.sportlerId)?.name ?? '—'}
          <div class="fokus-card">
            <div class="fokus-card-body">
              <div class="fokus-card-top">
                <span class="fokus-sportler-name">{sportlerName}</span>
                <div class="fokus-tags">
                  {#if fokus.kategorie}
                    <span class="fokus-tag fokus-tag-kategorie">{fokus.kategorie}</span>
                  {/if}
                  <span class="fokus-tag fokus-tag-schwerpunkt">{fokus.schwerpunkt}</span>
                  <span class="status-badge {statusClass(fokus.status)}" style="font-size:0.72rem;">
                    {statusLabel(fokus.status)}
                  </span>
                </div>
              </div>
              {#if fokus.notiz}
                <p class="fokus-notiz">{fokus.notiz}</p>
              {/if}
            </div>
            <div class="fokus-card-actions">
              <form method="POST" action="?/deleteTrainingsfokus" use:enhance>
                <input type="hidden" name="id" value={fokus.id} />
                <button type="submit" class="btn btn-outline-secondary btn-sm"
                  aria-label="Löschen"
                  onclick={(e) => { if (!confirm('Trainingsfokus wirklich löschen?')) e.preventDefault(); }}>
                  <i class="bi bi-trash"></i>
                </button>
              </form>
            </div>
          </div>
        {/each}
      </div>
    {:else}
      <div class="card text-center py-5" style="color: var(--text-muted);">
        <i class="bi bi-bullseye" style="font-size: 2.5rem; display: block; margin-bottom: 0.75rem;"></i>
        {#if data.sportlerName || data.kategorie}
          Keine Einträge für diese Suche. <a href="/trainingsfokus">Filter zurücksetzen</a>
        {:else}
          Noch keine Trainingsfokusse vorhanden
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
  </div>
</section>

<style>
  /* ── Sektionen ── */
  .tf-form-section {
    background: var(--bg-secondary);
    border-bottom: 1px solid var(--border-color);
    padding-bottom: 2.5rem;
  }

  .tf-list-section {
    background: var(--bg-page);
  }

  /* ── Form-Card ── */
  .tf-form-card {
    background: var(--bg-primary);
    border: 1px solid var(--border-color);
    border-radius: 14px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.07);
  }

  .tf-form-card-header {
    padding: 1rem 1.25rem;
    font-weight: 600;
    font-size: 0.95rem;
    color: var(--text-primary);
    border-bottom: 1px solid var(--border-color);
    background: var(--bg-secondary);
  }

  .tf-form-card-body {
    padding: 1.5rem 1.25rem;
  }

  /* ── Liste ── */
  .fokus-list {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
  }
  .fokus-card {
    display: flex;
    align-items: center;
    gap: 1rem;
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 0.9rem 1.1rem;
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    transition: transform 0.12s, box-shadow 0.12s;
  }
  .fokus-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 16px rgba(0,0,0,0.3);
  }
  .fokus-card-body {
    flex: 1;
    min-width: 0;
  }
  .fokus-card-top {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.6rem;
    margin-bottom: 0.3rem;
  }
  .fokus-sportler-name {
    font-size: 1rem;
    font-weight: 700;
    color: var(--text-primary);
    white-space: nowrap;
  }
  .fokus-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.35rem;
    align-items: center;
  }
  .fokus-tag {
    display: inline-flex;
    align-items: center;
    padding: 0.18rem 0.55rem;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 600;
    border: 1px solid;
  }
  .fokus-tag-kategorie {
    background: var(--accent-soft);
    color: var(--accent);
    border-color: var(--accent);
  }
  .fokus-tag-schwerpunkt {
    background: transparent;
    color: var(--text-primary);
    border-color: var(--border-color);
  }
  .fokus-notiz {
    margin: 0;
    font-size: 0.87rem;
    color: var(--text-muted);
    line-height: 1.5;
  }
  .fokus-card-actions {
    display: flex;
    align-items: center;
  }
</style>
