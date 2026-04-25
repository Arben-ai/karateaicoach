<script>
  import { enhance } from '$app/forms';
  import { marked } from 'marked';

  let { data } = $props();
  let trainingsfokusse = $derived(data.trainingsfokusse);

  let expandedId = $state(null);
  let localReadIds = $state(new Set());
  let generatingIds = $state(new Set());
  let localPlans = $state({});

  function getPlan(fokusId) {
    return localPlans[fokusId] ?? data.planByFokusId?.[fokusId] ?? null;
  }

  function isRead(fokus) {
    return fokus.gelesen || localReadIds.has(fokus.id);
  }

  let unreadCount = $derived(
    (trainingsfokusse ?? []).filter(f => !isRead(f)).length
  );

  async function toggle(fokus) {
    if (expandedId === fokus.id) {
      expandedId = null;
      return;
    }
    expandedId = fokus.id;
    if (!isRead(fokus)) {
      localReadIds = new Set([...localReadIds, fokus.id]);
      fetch('/api/mark-gelesen', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: fokus.id })
      });
    }
  }

  const kategorieColor = (k) => k === 'Kata'
    ? { bg: '#6f42c115', text: '#6f42c1', border: '#6f42c140' }
    : { bg: '#0d6efd15', text: '#0d6efd', border: '#0d6efd40' };

  function formatDate(iso) {
    if (!iso) return null;
    return new Date(iso).toLocaleDateString('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' });
  }

  function chatLink(fokus) {
    const msg = `Ich habe einen Trainingsplan erhalten für den Schwerpunkt: ${fokus.schwerpunkt}. Kannst du mir weitere Tipps geben?`;
    return `/chat?message=${encodeURIComponent(msg)}`;
  }
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-envelope-fill me-2"></i>Mein Feedback</h1>
  {#if unreadCount > 0}
    <span class="unread-counter">{unreadCount} ungelesen</span>
  {/if}
</div>
<p class="page-subtitle">Trainingsfeedback vom Coach — klicke auf eine Nachricht um Details zu sehen</p>

{#if trainingsfokusse && trainingsfokusse.length > 0}
  <div class="inbox">
    {#each trainingsfokusse as fokus (fokus.id)}
      {@const read = isRead(fokus)}
      {@const expanded = expandedId === fokus.id}
      {@const katColor = kategorieColor(fokus.kategorie)}
      {@const plan = getPlan(fokus.id)}
      {@const isGenerating = generatingIds.has(fokus.id)}

      <!-- svelte-ignore a11y_no_static_element_interactions -->
      <!-- svelte-ignore a11y_click_events_have_key_events -->
      <div
        class="inbox-row {read ? 'inbox-row--read' : 'inbox-row--unread'} {expanded ? 'inbox-row--expanded' : ''}"
        onclick={() => toggle(fokus)}
      >
        <!-- Header row -->
        <div class="inbox-row-header">
          <div class="d-flex align-items-center gap-2 flex-grow-1 min-w-0">
            {#if !read}
              <span class="unread-dot"></span>
            {:else}
              <span class="read-dot"></span>
            {/if}
            {#if fokus.kategorie}
              <span class="kategorie-pill" style="background:{katColor.bg}; color:{katColor.text}; border-color:{katColor.border};">
                {fokus.kategorie}
              </span>
            {/if}
            <span class="inbox-subject {read ? '' : 'fw-bold'}">{fokus.schwerpunkt}</span>
            {#if !expanded}
              <span class="inbox-preview d-none d-md-inline">— {fokus.beschreibung}</span>
            {/if}
          </div>

          <div class="d-flex align-items-center gap-2 flex-shrink-0">
            {#if plan}
              <span class="plan-ready-badge"><i class="bi bi-check-circle-fill me-1"></i>Plan vorhanden</span>
            {/if}
            {#if !read}
              <span class="neu-badge"><i class="bi bi-envelope me-1"></i>Neu</span>
            {/if}
            {#if fokus.status === 'INAKTIV'}
              <span class="archiv-badge"><i class="bi bi-archive me-1"></i>Archiviert</span>
            {/if}
            {#if fokus.createdAt}
              <span class="inbox-date">{formatDate(fokus.createdAt)}</span>
            {/if}
            <i class="bi bi-chevron-{expanded ? 'up' : 'down'} chevron-icon"></i>
          </div>
        </div>

        <!-- Expanded detail -->
        {#if expanded}
          <!-- svelte-ignore a11y_no_static_element_interactions -->
          <!-- svelte-ignore a11y_click_events_have_key_events -->
          <div class="inbox-detail" onclick={(e) => e.stopPropagation()}>
            <div class="detail-section">
              <span class="detail-label"><i class="bi bi-bullseye me-1"></i>Schwerpunkt</span>
              <span class="detail-value">{fokus.schwerpunkt}</span>
            </div>
            {#if fokus.kategorie}
              <div class="detail-section">
                <span class="detail-label"><i class="bi bi-tag me-1"></i>Kategorie</span>
                <span class="detail-value">{fokus.kategorie}</span>
              </div>
            {/if}
            <div class="detail-section">
              <span class="detail-label"><i class="bi bi-info-circle me-1"></i>Beschreibung</span>
              <span class="detail-value">{fokus.beschreibung}</span>
            </div>
            {#if fokus.notiz}
              <div class="coach-notiz">
                <div class="coach-notiz-label">
                  <i class="bi bi-chat-left-quote-fill me-2"></i>Notiz vom Coach
                </div>
                <p class="coach-notiz-text">{fokus.notiz}</p>
              </div>
            {/if}

            <!-- Plan section -->
            {#if fokus.status === 'AKTIV'}
              <div class="plan-area">
                {#if plan}
                  <!-- Plan exists: compact confirmation -->
                  <div class="plan-ready-row">
                    <div class="d-flex align-items-center gap-2">
                      <i class="bi bi-check-circle-fill" style="color: #28a745; font-size: 1.1rem;"></i>
                      <span style="font-size: 0.9rem; color: var(--text-primary);">
                        KI-Trainingsplan <strong>{plan.titel}</strong> wurde erstellt.
                      </span>
                    </div>
                    <a href="/meine-trainingsplaene?planId={plan.id}" class="btn btn-outline-success btn-sm">
                      <i class="bi bi-journal-text me-1"></i>Zum Trainingsplan
                    </a>
                  </div>
                {:else}
                  <!-- No plan yet: show generate button -->
                  <form
                    method="POST"
                    action="?/generatePlan"
                    use:enhance={() => {
                      generatingIds = new Set([...generatingIds, fokus.id]);
                      return async ({ result }) => {
                        generatingIds.delete(fokus.id);
                        generatingIds = new Set(generatingIds);
                        if (result.type === 'success' && result.data?.plan) {
                          localPlans = { ...localPlans, [fokus.id]: result.data.plan };
                        }
                      };
                    }}
                  >
                    <input type="hidden" name="fokusId" value={fokus.id} />
                    <button type="submit" class="btn btn-primary btn-sm" disabled={isGenerating}>
                      {#if isGenerating}
                        <span class="spinner-border spinner-border-sm me-2"></span>KI erstellt deinen Plan…
                      {:else}
                        <i class="bi bi-robot me-2"></i>Trainingsplan mit KI erstellen
                      {/if}
                    </button>
                    {#if isGenerating}
                      <p class="generating-hint">Das kann einige Sekunden dauern.</p>
                    {/if}
                  </form>
                {/if}
              </div>
            {/if}
          </div>
        {/if}
      </div>
    {/each}
  </div>
{:else}
  <div class="card">
    <div class="card-body text-center py-5" style="color: var(--text-muted);">
      <i class="bi bi-envelope-open" style="font-size: 2rem; display: block; margin-bottom: 0.5rem;"></i>
      Noch kein Feedback vom Coach vorhanden
    </div>
  </div>
{/if}

<style>
  .unread-counter {
    background: var(--accent);
    color: #fff;
    font-size: 0.75rem;
    font-weight: 700;
    padding: 0.25rem 0.7rem;
    border-radius: 20px;
  }

  .inbox {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .inbox-row {
    border-radius: 12px;
    border: 1px solid var(--border-color);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
    cursor: pointer;
    overflow: hidden;
    transition: box-shadow 0.15s, border-color 0.15s;
  }
  .inbox-row--unread {
    background: var(--card-bg);
    border-left: 3px solid var(--accent);
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.35);
  }
  .inbox-row--read {
    background: var(--card-bg);
    border-left: 3px solid transparent;
  }
  .inbox-row--expanded {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4);
    border-color: color-mix(in srgb, var(--accent) 35%, var(--border-color));
  }
  .inbox-row:hover {
    box-shadow: 0 4px 14px rgba(0, 0, 0, 0.4);
  }

  .inbox-row-header {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.9rem 1.1rem;
    min-width: 0;
  }

  .unread-dot {
    width: 9px; height: 9px;
    border-radius: 50%;
    background: var(--accent);
    flex-shrink: 0;
  }
  .read-dot {
    width: 9px; height: 9px;
    border-radius: 50%;
    background: transparent;
    flex-shrink: 0;
  }

  .kategorie-pill {
    font-size: 0.72rem;
    font-weight: 600;
    padding: 0.2rem 0.55rem;
    border-radius: 20px;
    border: 1px solid;
    white-space: nowrap;
    flex-shrink: 0;
  }

  .inbox-subject {
    font-size: 0.92rem;
    color: var(--text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .inbox-preview {
    font-size: 0.85rem;
    color: var(--text-muted);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    flex-shrink: 1;
    min-width: 0;
  }

  .plan-ready-badge {
    font-size: 0.72rem;
    font-weight: 600;
    color: #28a745;
    background: #28a74515;
    border: 1px solid #28a74540;
    padding: 0.2rem 0.55rem;
    border-radius: 20px;
    white-space: nowrap;
  }

  .neu-badge {
    font-size: 0.72rem;
    font-weight: 700;
    color: var(--accent);
    background: color-mix(in srgb, var(--accent) 12%, transparent);
    border: 1px solid color-mix(in srgb, var(--accent) 35%, transparent);
    padding: 0.2rem 0.55rem;
    border-radius: 20px;
    white-space: nowrap;
  }

  .archiv-badge {
    font-size: 0.72rem;
    font-weight: 600;
    color: var(--text-muted);
    background: color-mix(in srgb, var(--text-muted) 10%, transparent);
    border: 1px solid var(--border-color);
    padding: 0.2rem 0.55rem;
    border-radius: 20px;
    white-space: nowrap;
  }

  .inbox-date {
    font-size: 0.78rem;
    color: var(--text-muted);
    white-space: nowrap;
  }

  .chevron-icon {
    font-size: 0.8rem;
    color: var(--text-muted);
  }

  .inbox-detail {
    padding: 0 1.1rem 1.25rem 2.5rem;
    border-top: 1px solid var(--border-color);
    padding-top: 1rem;
  }

  .detail-section {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
    font-size: 0.88rem;
  }
  .detail-label {
    color: var(--text-muted);
    min-width: 120px;
    flex-shrink: 0;
  }
  .detail-value { color: var(--text-primary); }

  .coach-notiz {
    margin-top: 0.75rem;
    background: color-mix(in srgb, var(--accent) 8%, transparent);
    border: 1px solid color-mix(in srgb, var(--accent) 25%, transparent);
    border-radius: 8px;
    padding: 0.75rem 1rem;
  }
  .coach-notiz-label {
    font-size: 0.78rem;
    font-weight: 700;
    color: var(--accent);
    margin-bottom: 0.4rem;
    text-transform: uppercase;
    letter-spacing: 0.04em;
  }
  .coach-notiz-text {
    font-size: 0.9rem;
    color: var(--text-primary);
    margin: 0;
    line-height: 1.6;
    font-style: italic;
  }

  .plan-area {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid var(--border-color);
  }

  .generating-hint {
    font-size: 0.8rem;
    color: var(--text-muted);
    margin-top: 0.5rem;
    margin-bottom: 0;
  }

  .plan-ready-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    padding: 0.75rem 1rem;
    background: color-mix(in srgb, #28a745 6%, transparent);
    border: 1px solid color-mix(in srgb, #28a745 25%, transparent);
    border-radius: 8px;
    flex-wrap: wrap;
  }
</style>
