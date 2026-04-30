<script>
  import { marked } from 'marked';
  import { onMount, tick } from 'svelte';

  let { data } = $props();
  let trainingsplaene = $derived(data.trainingsplaene);
  let pagination = $derived(data.pagination);

  let expandedId = $state(null);
  let flashingId = $state(null);

  onMount(async () => {
    const id = data.highlightPlanId;
    if (!id) return;
    expandedId = id;
    await tick();
    const el = document.getElementById(`plan-${id}`);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'center' });
      flashingId = id;
      setTimeout(() => { flashingId = null; }, 1800);
    }
  });

  function toggle(id) {
    expandedId = expandedId === id ? null : id;
  }

  const createPageLink = (page) => `/meine-trainingsplaene?page=${page}`;

  const statusConfig = (status) => ({
    DRAFT:     { label: 'Entwurf',       cls: 'badge-status-draft' },
    ACTIVE:    { label: 'Aktiv',         cls: 'badge-status-active' },
    COMPLETED: { label: 'Abgeschlossen', cls: 'badge-status-completed' },
    ARCHIVED:  { label: 'Archiviert',    cls: 'badge-status-archived' },
  }[status] ?? { label: status, cls: 'badge-status-draft' });

  function formatDate(iso) {
    if (!iso) return null;
    return new Date(iso).toLocaleDateString('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' });
  }

  function chatLink(plan) {
    const msg = `Ich habe einen Trainingsplan mit dem Fokus "${plan.fokus ?? plan.titel}". Kannst du mir weitere Tipps geben oder den Plan anpassen?`;
    return `/chat?message=${encodeURIComponent(msg)}`;
  }
</script>

<div class="page-content">
<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-journal-text me-2"></i>Meine Trainingspläne</h1>
  {#if pagination}
    <span class="pagination-info">{pagination.totalElements} Plan{pagination.totalElements !== 1 ? 'e' : ''} total</span>
  {/if}
</div>
<p class="page-subtitle">Von der KI generierte Pläne basierend auf deinem Coach-Feedback</p>

{#if trainingsplaene && trainingsplaene.length > 0}
  <div class="plan-list">
    {#each trainingsplaene as plan (plan.id)}
      {@const cfg = statusConfig(plan.status)}
      {@const expanded = expandedId === plan.id}

      <div id="plan-{plan.id}" class="plan-card {expanded ? 'plan-card--expanded' : ''} {flashingId === plan.id ? 'plan-card--flash' : ''}">
        <!-- Header: always visible, click to expand -->
        <!-- svelte-ignore a11y_no_static_element_interactions -->
        <!-- svelte-ignore a11y_click_events_have_key_events -->
        <div class="plan-card-header" onclick={() => toggle(plan.id)}>
          <div class="d-flex align-items-center gap-2 flex-grow-1 min-w-0">
            <i class="bi bi-robot plan-icon"></i>
            <div class="min-w-0">
              <div class="plan-titel {expanded ? 'fw-bold' : ''}">{plan.titel}</div>
              {#if plan.fokus}
                <div class="plan-fokus"><i class="bi bi-bullseye me-1"></i>{plan.fokus}</div>
              {/if}
            </div>
          </div>
          <div class="d-flex align-items-center gap-2 flex-shrink-0">
            {#if plan.erstelldatum}
              <span class="plan-date">{formatDate(plan.erstelldatum)}</span>
            {/if}
            <span class="status-badge {cfg.cls}">{cfg.label}</span>
            <i class="bi bi-chevron-{expanded ? 'up' : 'down'} chevron-icon"></i>
          </div>
        </div>

        <!-- Body: plan content, only when expanded -->
        {#if expanded}
          <div class="plan-card-body">
            {#if plan.inhalt}
              <div class="plan-content">
                {@html marked(plan.inhalt)}
              </div>
            {:else}
              <p style="color: var(--text-muted); font-style: italic;">Kein Inhalt vorhanden.</p>
            {/if}
            <div class="plan-card-footer">
              <a href={chatLink(plan)} class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-chat-dots me-1"></i>Im Chat weiter besprechen
              </a>
            </div>
          </div>
        {/if}
      </div>
    {/each}
  </div>

  {#if pagination && pagination.totalPages > 1}
    <div class="d-flex justify-content-between align-items-center mt-3">
      <span class="pagination-info">Seite {pagination.page + 1} von {pagination.totalPages}</span>
      <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary btn-sm"
          class:disabled={pagination.page === 0}
          href={pagination.page === 0 ? '#' : createPageLink(pagination.page - 1)}
          data-sveltekit-noscroll>
          <i class="bi bi-chevron-left me-1"></i>Zurück
        </a>
        <a class="btn btn-outline-secondary btn-sm"
          class:disabled={pagination.page >= pagination.totalPages - 1}
          href={pagination.page >= pagination.totalPages - 1 ? '#' : createPageLink(pagination.page + 1)}
          data-sveltekit-noscroll>
          Weiter<i class="bi bi-chevron-right ms-1"></i>
        </a>
      </div>
    </div>
  {/if}

{:else}
  <div class="card">
    <div class="card-body text-center py-5" style="color: var(--text-muted);">
      <i class="bi bi-journal-x" style="font-size: 2.5rem; display: block; margin-bottom: 0.75rem;"></i>
      <p class="mb-3">Noch keine Trainingspläne vorhanden.</p>
      <a href="/mein-feedback" class="btn btn-primary btn-sm">
        <i class="bi bi-bullseye me-1"></i>Zum Feedback — Plan erstellen
      </a>
    </div>
  </div>
{/if}

</div><!-- /page-content -->

<style>
  .plan-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .plan-card {
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
    transition: box-shadow 0.15s, border-color 0.15s;
  }
  .plan-card:hover {
    box-shadow: 0 4px 14px rgba(0, 0, 0, 0.4);
  }
  .plan-card--expanded {
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.45);
    border-color: color-mix(in srgb, var(--accent) 40%, var(--border-color));
  }

  .plan-card-header {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem 1.25rem;
    cursor: pointer;
    transition: background 0.15s;
  }
  .plan-card-header:hover {
    background: color-mix(in srgb, var(--accent) 5%, transparent);
  }

  .plan-icon {
    font-size: 1.2rem;
    color: var(--accent);
    flex-shrink: 0;
  }

  .plan-titel {
    font-size: 0.95rem;
    color: var(--text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .plan-fokus {
    font-size: 0.78rem;
    color: var(--text-muted);
    margin-top: 0.1rem;
  }

  .plan-date {
    font-size: 0.78rem;
    color: var(--text-muted);
    white-space: nowrap;
  }

  .chevron-icon {
    font-size: 0.8rem;
    color: var(--text-muted);
  }

  .plan-card-body {
    border-top: 1px solid var(--border-color);
    padding: 1.25rem;
  }

  .plan-content {
    font-size: 0.88rem;
    color: var(--text-primary);
    line-height: 1.75;
    max-height: 500px;
    overflow-y: auto;
    padding-right: 0.25rem;
  }

  .plan-content :global(h1),
  .plan-content :global(h2),
  .plan-content :global(h3) {
    font-size: 0.97rem;
    font-weight: 700;
    margin-top: 1.1rem;
    margin-bottom: 0.35rem;
    color: var(--text-primary);
  }
  .plan-content :global(ul),
  .plan-content :global(ol) {
    padding-left: 1.25rem;
    margin-bottom: 0.5rem;
  }
  .plan-content :global(li) { margin-bottom: 0.2rem; }
  .plan-content :global(p) { margin-bottom: 0.5rem; }
  .plan-content :global(strong) { color: var(--text-primary); }

  .plan-card-footer {
    margin-top: 1rem;
    padding-top: 0.75rem;
    border-top: 1px solid var(--border-color);
  }

  @keyframes plan-flash {
    0%   { box-shadow: 0 0 0 3px var(--accent), 0 4px 18px rgba(0,0,0,0.45); border-color: var(--accent); }
    65%  { box-shadow: 0 0 0 3px var(--accent), 0 4px 18px rgba(0,0,0,0.45); border-color: var(--accent); }
    100% { box-shadow: 0 2px 8px rgba(0,0,0,0.3); border-color: var(--border-color); }
  }
  .plan-card--flash {
    animation: plan-flash 1.8s ease forwards;
  }
</style>
