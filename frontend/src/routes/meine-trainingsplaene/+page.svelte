<script>
  let { data } = $props();
  let trainingsplaene = $derived(data.trainingsplaene);
  let pagination = $derived(data.pagination);

  const createPageLink = (page) => `/meine-trainingsplaene?page=${page}`;

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
  <h1 class="page-title"><i class="bi bi-journal-text me-2"></i>Meine Trainingspläne</h1>
</div>
<p class="page-subtitle">Aktuelle und vergangene Trainingspläne</p>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h2 class="h5 mb-0" style="color: var(--text-muted);">
    <i class="bi bi-list-ul me-2"></i>Übersicht
  </h2>
  {#if pagination}
    <span class="pagination-info">{pagination.totalElements} Pläne total</span>
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
            </tr>
          {/each}
        {:else}
          <tr>
            <td colspan="3" class="text-center py-5" style="color: var(--text-muted);">
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
