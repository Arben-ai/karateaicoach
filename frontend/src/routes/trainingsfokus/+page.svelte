<script>
  import { enhance } from "$app/forms";

  let { data, form } = $props();
  let trainingsfokusse = $derived(data.trainingsfokus);
  let sportler = $derived(data.sportler);
  let pagination = $derived(data.pagination);

  const createPageLink = (page) => `/trainingsfokus?page=${page}&size=${pagination.size}`;

  const statusClass = (status) =>
    status === "AKTIV" ? "badge-status-active" : "badge-status-archived";

  const statusLabel = (status) =>
    status === "AKTIV" ? "Aktiv" : "Inaktiv";
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-bullseye me-2"></i>Trainingsfokus</h1>
</div>
<p class="page-subtitle">Trainingsschwerpunkte für Sportler definieren</p>

<!-- Formular -->
<div class="card mb-4">
  <div class="card-header">
    <i class="bi bi-plus-circle me-2"></i>Neuen Trainingsfokus erfassen
  </div>
  <div class="card-body p-4">
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
        <div class="col-md-6">
          <label class="form-label" for="schwerpunkt">Schwerpunkt</label>
          <input class="form-control" id="schwerpunkt" name="schwerpunkt" type="text"
            placeholder="z.B. Kata, Kumite, Kondition" required />
        </div>
        <div class="col-md-6">
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
        <div class="col-md-8">
          <label class="form-label" for="beschreibung">Beschreibung</label>
          <input class="form-control" id="beschreibung" name="beschreibung" type="text"
            placeholder="Kurze Beschreibung des Trainingsfokus" required />
        </div>
        <div class="col-md-4">
          <label class="form-label" for="status">Status</label>
          <select class="form-select" id="status" name="status" required>
            <option value="">Status wählen...</option>
            <option value="AKTIV">Aktiv</option>
            <option value="INAKTIV">Inaktiv</option>
          </select>
        </div>
      </div>
      <button type="submit" class="btn btn-primary">
        <i class="bi bi-plus-circle me-2"></i>Fokus hinzufügen
      </button>
    </form>
  </div>
</div>

<hr class="section-divider" />

<!-- Tabelle -->
<div class="d-flex justify-content-between align-items-center mb-3">
  <h2 class="h5 mb-0" style="color: var(--text-muted);">
    <i class="bi bi-list-ul me-2"></i>Übersicht
  </h2>
  {#if pagination}
    <span class="pagination-info">{pagination.totalElements} Einträge total</span>
  {/if}
</div>

<div class="card">
  <div class="table-responsive">
    <table class="table mb-0">
      <thead>
        <tr>
          <th>Schwerpunkt</th>
          <th>Beschreibung</th>
          <th>Status</th>
          <th>Sportler</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {#if trainingsfokusse && trainingsfokusse.length > 0}
          {#each trainingsfokusse as fokus}
            <tr>
              <td class="fw-500">{fokus.schwerpunkt}</td>
              <td style="color: var(--text-muted);">{fokus.beschreibung}</td>
              <td>
                <span class="status-badge {statusClass(fokus.status)}">
                  {statusLabel(fokus.status)}
                </span>
              </td>
              <td>{sportler.find(s => s.id === fokus.sportlerId)?.name ?? fokus.sportlerId}</td>
              <td>
                <form method="POST" action="?/deleteTrainingsfokus" use:enhance>
                  <input type="hidden" name="id" value={fokus.id} />
                  <button type="submit" class="btn btn-outline-secondary btn-sm"
                    aria-label="Löschen"
                    onclick={() => confirm('Trainingsfokus wirklich löschen?')}>
                    <i class="bi bi-trash"></i>
                  </button>
                </form>
              </td>
            </tr>
          {/each}
        {:else}
          <tr>
            <td colspan="5" class="text-center py-5" style="color: var(--text-muted);">
              <i class="bi bi-bullseye" style="font-size: 2rem; display: block; margin-bottom: 0.5rem;"></i>
              Noch keine Trainingsfokusse vorhanden
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
