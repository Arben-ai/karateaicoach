<script>
  import { marked } from 'marked';
  import { onMount, tick } from 'svelte';

  let { data } = $props();
  let trainingsplaene = $derived(data.trainingsplaene);
  let pagination = $derived(data.pagination);

  let expandedId = $state(null);
  let flashingId = $state(null);
  let expandedSections = $state({});

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
    if (expandedId === id) expandedSections = {};
  }

  function toggleSection(planId, sectionIdx) {
    const key = `${planId}-${sectionIdx}`;
    expandedSections = { ...expandedSections, [key]: !expandedSections[key] };
  }

  function isCollapsibleSection(title) {
    if (!title) return false;
    return /^(woche|phase|block|einheit|tag|monat|zyklus)\s*\d+/i.test(title);
  }

  function isSectionExpanded(planId, sectionIdx, title) {
    const key = `${planId}-${sectionIdx}`;
    if (key in expandedSections) return expandedSections[key];
    return false; // collapsible sections start closed
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

  function parseSections(markdown) {
    if (!markdown) return [];
    const lines = markdown.split('\n');

    // 1. Try markdown headings (###, ##, etc.)
    let minLevel = null;
    for (const line of lines) {
      const m = line.match(/^(#{1,6}) /);
      if (m) {
        const lvl = m[1].length;
        if (minLevel === null || lvl < minLevel) minLevel = lvl;
      }
    }

    if (minLevel !== null) {
      const prefix = '#'.repeat(minLevel) + ' ';
      return splitAtHeaders(lines,
        (line) => line.startsWith(prefix),
        (line) => line.replace(/^#+\s+/, ''));
    }

    // 2. Fallback: detect standalone short lines preceded by a blank line.
    // Catches plain-text headers like "Kurzziel für Laura Fischer", "Woche 1 (Basis)", etc.
    const isStandaloneHeader = (line, prevLine) => {
      const t = line.trim();
      if (!t || t.length > 80) return false;
      if (/^[-*#]/.test(t)) return false;      // list markers / headings
      if (/^\d+[.)]\s/.test(t)) return false;  // numbered list items
      if (/[,]$/.test(t)) return false;         // ends with comma → sentence fragment
      return (prevLine ?? '').trim() === '';    // must follow a blank line
    };

    const sections = [];
    let title = null;
    let buf = [];
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];
      if (isStandaloneHeader(line, lines[i - 1] ?? '')) {
        if (title !== null || buf.join('').trim()) {
          sections.push({ title, content: buf.join('\n').trim() });
        }
        title = line.trim().replace(/^\*\*|\*\*$/g, '');
        buf = [];
      } else {
        buf.push(line);
      }
    }
    if (title !== null || buf.join('').trim()) {
      sections.push({ title, content: buf.join('\n').trim() });
    }

    return sections.length > 1 ? sections : [{ title: null, content: markdown.trim() }];
  }

  function splitAtHeaders(lines, isHeader, getTitle) {
    const sections = [];
    let title = null;
    let buf = [];
    for (const line of lines) {
      if (isHeader(line)) {
        if (title !== null || buf.join('').trim()) sections.push({ title, content: buf.join('\n').trim() });
        title = getTitle(line).trim().replace(/^\*\*|\*\*$/g, '');
        buf = [];
      } else {
        buf.push(line);
      }
    }
    if (title !== null || buf.join('').trim()) sections.push({ title, content: buf.join('\n').trim() });
    return sections;
  }
</script>

<div class="page-content">
<div class="d-flex justify-content-between align-items-center mb-1" style="animation: ka-slide-in-right 0.4s ease both;">
  <h1 class="page-title"><i class="bi bi-journal-text me-2"></i>Meine Trainingspläne</h1>
  {#if pagination}
    <span class="pagination-info">{pagination.totalElements} {pagination.totalElements !== 1 ? 'Pläne' : 'Plan'} total</span>
  {/if}
</div>
<p class="page-subtitle">Von der KI generierte Pläne basierend auf deinem Coach-Feedback</p>

{#if trainingsplaene && trainingsplaene.length > 0}
  <div class="plan-list">
    {#each trainingsplaene as plan, i (plan.id)}
      {@const cfg = statusConfig(plan.status)}
      {@const expanded = expandedId === plan.id}

      <div id="plan-{plan.id}" class="plan-card {expanded ? 'plan-card--expanded' : ''} {flashingId === plan.id ? 'plan-card--flash' : ''}" style="animation: ka-slide-up 0.45s ease both; animation-delay: {i * 0.07}s;">
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
              {@const sections = parseSections(plan.inhalt)}
              <div class="plan-sections">
                {#each sections as section, i}
                  {@const collapsible = isCollapsibleSection(section.title)}
                  {@const secExpanded = collapsible ? isSectionExpanded(plan.id, i, section.title) : true}
                  {#if section.title}
                    <div class="plan-section">
                      <!-- svelte-ignore a11y_no_static_element_interactions -->
                      <!-- svelte-ignore a11y_click_events_have_key_events -->
                      <div
                        class="plan-section-header {collapsible ? 'plan-section-header--collapsible' : ''}"
                        onclick={() => collapsible && toggleSection(plan.id, i)}
                      >
                        <span>
                          <i class="bi bi-calendar-week me-2"></i>{section.title}
                        </span>
                        {#if collapsible}
                          <i class="bi bi-chevron-{secExpanded ? 'up' : 'down'} section-chevron"></i>
                        {/if}
                      </div>
                      {#if secExpanded && section.content}
                        <div class="plan-section-body">
                          {@html marked(section.content)}
                        </div>
                      {/if}
                    </div>
                  {:else if section.content}
                    <div class="plan-section">
                      <div class="plan-section-header plan-section-header--intro">
                        <span><i class="bi bi-info-circle me-2"></i>Übersicht</span>
                      </div>
                      <div class="plan-section-body">
                        {@html marked(section.content)}
                      </div>
                    </div>
                  {/if}
                {/each}
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
    padding: 1.5rem 1.5rem 1.25rem;
    background: var(--bg-page);
  }

  .plan-sections {
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }

  .plan-section {
    border: 1px solid var(--border-color);
    border-radius: 10px;
    overflow: hidden;
    background: var(--bg-primary);
  }

  .plan-section-header {
    background: var(--bg-secondary);
    border-bottom: 1px solid var(--border-color);
    padding: 0.6rem 1rem;
    font-size: 0.82rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.06em;
    color: var(--accent);
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .plan-section-header--collapsible {
    cursor: pointer;
    user-select: none;
    transition: background 0.15s;
  }

  .plan-section-header--collapsible:hover {
    background: color-mix(in srgb, var(--accent) 8%, var(--bg-secondary));
  }

  .section-chevron {
    font-size: 0.75rem;
    color: var(--text-muted);
    flex-shrink: 0;
  }

  .plan-section-header--intro {
    color: var(--text-muted);
  }

  .plan-section-body {
    padding: 0.9rem 1rem;
    font-size: 0.88rem;
    color: var(--text-primary);
    line-height: 1.7;
    border-top: 1px solid var(--border-color);
  }

  .plan-section-body :global(h3),
  .plan-section-body :global(h4) {
    font-size: 0.88rem;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0.85rem 0 0.35rem;
    padding-bottom: 0.2rem;
    border-bottom: 1px solid var(--border-color);
  }
  .plan-section-body :global(h3:first-child),
  .plan-section-body :global(h4:first-child) { margin-top: 0; }
  .plan-section-body :global(ul),
  .plan-section-body :global(ol) { padding-left: 1.3rem; margin-bottom: 0.5rem; }
  .plan-section-body :global(li) { margin-bottom: 0.25rem; }
  .plan-section-body :global(p) { margin-bottom: 0.5rem; }
  .plan-section-body :global(p:last-child) { margin-bottom: 0; }
  .plan-section-body :global(strong) { font-weight: 600; color: var(--text-primary); }

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
