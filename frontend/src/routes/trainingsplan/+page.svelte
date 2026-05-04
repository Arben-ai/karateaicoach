<script>
  import { goto } from "$app/navigation";

  let { data } = $props();
  let sportler = $derived(data.sportler);
  let trainingsplaene = $derived(data.trainingsplan);
  let pagination = $derived(data.trainingsplanPagination);

  let searchInput = $state("");
  let statusFilter = $state("");

  $effect(() => {
    searchInput = data.sportlerName ?? "";
    statusFilter = data.status ?? "";
  });

  const buildLink = (page, s = searchInput, st = statusFilter) => {
    const p = new URLSearchParams({ page, size: pagination.size });
    if (s) p.set("sportlerName", s);
    if (st) p.set("status", st);
    return `/trainingsplan?${p}`;
  };

  const createPageLink = (page) => buildLink(page);

  function applyFilter() {
    goto(buildLink(0), { noScroll: true });
  }

  const statusClass = (status) => {
    switch (status) {
      case "DRAFT":     return "badge-status-draft";
      case "ACTIVE":    return "badge-status-active";
      case "COMPLETED": return "badge-status-completed";
      case "ARCHIVED":  return "badge-status-archived";
      default:          return "badge-status-draft";
    }
  };

  const statusLabel = (status) => {
    switch (status) {
      case "DRAFT":     return "Entwurf";
      case "ACTIVE":    return "Aktiv";
      case "COMPLETED": return "Abgeschlossen";
      case "ARCHIVED":  return "Archiviert";
      default:          return status;
    }
  };

  const statusAccent = (status) => {
    switch (status) {
      case "ACTIVE":    return "#28a745";
      case "COMPLETED": return "#007bff";
      case "ARCHIVED":  return "#6c757d";
      default:          return "var(--border-color)";
    }
  };

  const formatDate = (date) => {
    if (!date) return null;
    return new Date(date).toLocaleDateString("de-CH", { day: "2-digit", month: "2-digit", year: "numeric" });
  };
</script>

<div class="page-content" style="animation: ka-slide-down 0.4s ease both;">
<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-journal-text me-2"></i>Trainingspläne</h1>
</div>
<p class="page-subtitle">Übersicht der KI-generierten Trainingspläne aller Sportler</p>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h2 class="h5 mb-0" style="color: var(--text-muted);">
    <i class="bi bi-list-ul me-2"></i>Alle Pläne
  </h2>
  {#if pagination}
    <span class="pagination-info">{pagination.totalElements} Pläne total</span>
  {/if}
</div>

<div class="row g-2 mb-3">
  <div class="col-sm-6 col-md-5">
    <div class="input-group">
      <span class="input-group-text" style="background:var(--card-bg); border-color:var(--border-color); color:var(--text-muted);">
        <i class="bi bi-search"></i>
      </span>
      <input
        class="form-control"
        type="text"
        placeholder="Sportler suchen..."
        bind:value={searchInput}
        onkeydown={(e) => e.key === "Enter" && applyFilter()}
        style="background:var(--card-bg); border-color:var(--border-color); color:var(--text-primary);"
      />
    </div>
  </div>
  <div class="col-sm-3 col-md-3">
    <select
      class="form-select"
      bind:value={statusFilter}
      onchange={applyFilter}
      style="background:var(--card-bg); border-color:var(--border-color); color:var(--text-primary);"
    >
      <option value="">Alle Status</option>
      <option value="ACTIVE">Aktiv</option>
      <option value="COMPLETED">Abgeschlossen</option>
      <option value="DRAFT">Entwurf</option>
      <option value="ARCHIVED">Archiviert</option>
    </select>
  </div>
  <div class="col-sm-2 col-md-2">
    <button class="btn btn-primary w-100" onclick={applyFilter}>
      <i class="bi bi-search me-1"></i>Suchen
    </button>
  </div>
  {#if searchInput || statusFilter}
    <div class="col-sm-2 col-md-2">
      <a href="/trainingsplan" class="btn btn-outline-secondary w-100" data-sveltekit-noscroll>
        <i class="bi bi-x-circle me-1"></i>Reset
      </a>
    </div>
  {/if}
</div>

{#if trainingsplaene && trainingsplaene.length > 0}
  <div class="plan-list">
    {#each trainingsplaene as tp, i}
      {@const sportlerName = sportler.find(s => s.id === tp.sportlerId)?.name ?? '—'}
      {@const accent = statusAccent(tp.status)}
      <div class="plan-card" style="border-left-color: {accent}; animation: ka-slide-up 0.42s ease both; animation-delay: {i * 0.06}s;">
        <div class="plan-card-body">
          <div class="plan-card-top">
            <span class="plan-sportler-name">{sportlerName}</span>
            <div class="plan-card-meta">
              {#if tp.erstelldatum}
                <span class="plan-date">
                  <i class="bi bi-calendar3 me-1"></i>{formatDate(tp.erstelldatum)}
                </span>
              {/if}
              <span class="status-badge {statusClass(tp.status)}">{statusLabel(tp.status)}</span>
            </div>
          </div>
          <div class="plan-titel">{tp.titel}</div>
          <div class="plan-chips">
            {#if tp.fokus}
              <span class="plan-chip plan-chip-fokus">
                <i class="bi bi-bullseye me-1"></i>{tp.fokus}
              </span>
            {/if}
            <span class="plan-chip">
              <i class="bi bi-clock me-1"></i>{tp.dauer} Min.
            </span>
          </div>
          {#if tp.inhalt}
            <p class="plan-inhalt">
              {tp.inhalt.length > 150 ? tp.inhalt.slice(0, 150) + '…' : tp.inhalt}
            </p>
          {/if}
        </div>
      </div>
    {/each}
  </div>
{:else}
  <div class="card text-center py-5" style="color: var(--text-muted);">
    <i class="bi bi-journal-x" style="font-size: 2.5rem; display: block; margin-bottom: 0.75rem;"></i>
    {#if data.sportlerName || data.status}
      Keine Pläne für diese Suche. <a href="/trainingsplan">Filter zurücksetzen</a>
    {:else}
      Noch keine Trainingspläne vorhanden
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

</div><!-- /page-content -->

<style>
  .plan-list {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
  }
  .plan-card {
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-left: 4px solid var(--border-color);
    border-radius: 12px;
    padding: 1rem 1.2rem;
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    transition: transform 0.12s, box-shadow 0.12s;
  }
  .plan-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 16px rgba(0,0,0,0.3);
  }
  .plan-card-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-bottom: 0.25rem;
  }
  .plan-sportler-name {
    font-size: 1rem;
    font-weight: 700;
    color: var(--text-primary);
  }
  .plan-card-meta {
    display: flex;
    align-items: center;
    gap: 0.6rem;
  }
  .plan-date {
    font-size: 0.78rem;
    color: var(--text-muted);
  }
  .plan-titel {
    font-size: 0.9rem;
    color: var(--text-muted);
    margin-bottom: 0.45rem;
  }
  .plan-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 0.35rem;
    margin-bottom: 0.5rem;
  }
  .plan-chip {
    display: inline-flex;
    align-items: center;
    padding: 0.18rem 0.55rem;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 500;
    background: transparent;
    color: var(--text-muted);
    border: 1px solid var(--border-color);
  }
  .plan-chip-fokus {
    background: var(--accent-soft);
    color: var(--accent);
    border-color: var(--accent);
    font-weight: 600;
  }
  .plan-inhalt {
    margin: 0;
    font-size: 0.83rem;
    color: var(--text-muted);
    line-height: 1.55;
    border-left: 2px solid var(--border-color);
    padding-left: 0.75rem;
  }
</style>
