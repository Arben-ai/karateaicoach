<script>
  import { enhance } from "$app/forms";

  let { data, form } = $props();
  let sportler = $derived(data.sportler);
  let trainingsplaene = $derived(data.trainingsplan);
  let pagination = $derived(data.trainingsplanPagination);

  const createPageLink = (page) => `/trainingsplan?page=${page}&size=${pagination.size}`;

  const statusClass = (status) => {
    switch (status) {
      case "DRAFT": return "badge-status-draft";
      case "ACTIVE": return "badge-status-active";
      case "COMPLETED": return "badge-status-completed";
      case "ARCHIVED": return "badge-status-archived";
      default: return "badge-status-draft";
    }
  };

  const statusLabel = (status) => {
    switch (status) {
      case "DRAFT": return "Entwurf";
      case "ACTIVE": return "Aktiv";
      case "COMPLETED": return "Abgeschlossen";
      case "ARCHIVED": return "Archiviert";
      default: return status;
    }
  };
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-journal-text me-2"></i>Trainingspläne</h1>
</div>
<p class="page-subtitle">Trainingspläne erstellen und verwalten</p>

<!-- Formular -->
<div class="card mb-4">
  <div class="card-header">
    <i class="bi bi-plus-circle me-2"></i>Neuen Trainingsplan erstellen
  </div>
  <div class="card-body p-4">
    {#if form?.success}
      <div class="alert alert-success" role="alert">
        <i class="bi bi-check-circle me-2"></i>Trainingsplan erfolgreich erstellt!
      </div>
    {/if}
    {#if form?.error}
      <div class="alert alert-danger" role="alert">
        <i class="bi bi-exclamation-circle me-2"></i>{form.error}
      </div>
    {/if}

    <form method="POST" action="?/createTrainingsplan" use:enhance>
      <div class="row g-3 mb-3">
        <div class="col-md-12">
          <label class="form-label" for="titel">Titel</label>
          <input class="form-control" id="titel" name="titel" type="text" placeholder="z.B. Kata-Training Fortgeschrittene" required />
        </div>
      </div>
      <div class="row g-3 mb-3">
        <div class="col-md-4">
          <label class="form-label" for="dauer">Dauer (Minuten)</label>
          <input class="form-control" id="dauer" name="dauer" type="number" min="1" placeholder="60" required />
        </div>
        <div class="col-md-4">
          <label class="form-label" for="status">Status</label>
          <select class="form-select" id="status" name="status" required>
            <option value="">Status wählen...</option>
            <option value="DRAFT">Entwurf</option>
            <option value="ACTIVE">Aktiv</option>
            <option value="COMPLETED">Abgeschlossen</option>
            <option value="ARCHIVED">Archiviert</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label" for="sportler">Sportler</label>
          <select class="form-select" id="sportler" name="sportlerId" required>
            <option value="">Sportler wählen...</option>
            {#each sportler as s}
              <option value={s.id}>{s.name}</option>
            {/each}
          </select>
        </div>
      </div>
      <button type="submit" class="btn btn-primary">
        <i class="bi bi-plus-circle me-2"></i>Plan erstellen
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
    <span class="pagination-info">
      {pagination.totalElements} Pläne total
    </span>
  {/if}
</div>

<div class="card">
  <div class="table-responsive">
    <table class="table mb-0">
      <thead>
        <tr>
          <th>Titel</th>
          <th>Dauer</th>
          <th>Status</th>
          <th>Sportler</th>
        </tr>
      </thead>
      <tbody>
        {#if trainingsplaene && trainingsplaene.length > 0}
          {#each trainingsplaene as tp}
            <tr>
              <td class="fw-500">{tp.titel}</td>
              <td style="color: var(--text-muted);">
                <i class="bi bi-clock me-1"></i>{tp.dauer} Min.
              </td>
              <td>
                <span class="status-badge {statusClass(tp.status)}">
                  {statusLabel(tp.status)}
                </span>
              </td>
              <td>
                <code style="color: var(--text-muted); font-size: 0.75rem;">{tp.sportlerId}</code>
              </td>
            </tr>
          {/each}
        {:else}
          <tr>
            <td colspan="4" class="text-center py-5" style="color: var(--text-muted);">
              <i class="bi bi-journal-x" style="font-size: 2rem; display: block; margin-bottom: 0.5rem;"></i>
              Noch keine Trainingspläne vorhanden
            </td>
          </tr>
        {/if}
      </tbody>
    </table>
  </div>
</div>

{#if pagination && pagination.totalPages > 1}
  <div class="d-flex justify-content-between align-items-center mt-3">
    <span class="pagination-info">
      Seite {pagination.page + 1} von {pagination.totalPages}
    </span>
    <div class="d-flex gap-2">
      <a
        class="btn btn-outline-secondary btn-sm"
        class:disabled={pagination.page === 0}
        href={pagination.page === 0 ? "#" : createPageLink(pagination.page - 1)}
      >
        <i class="bi bi-chevron-left me-1"></i>Zurück
      </a>
      <a
        class="btn btn-outline-secondary btn-sm"
        class:disabled={pagination.page >= pagination.totalPages - 1}
        href={pagination.page >= pagination.totalPages - 1 ? "#" : createPageLink(pagination.page + 1)}
      >
        Weiter<i class="bi bi-chevron-right ms-1"></i>
      </a>
    </div>
  </div>
{/if}
