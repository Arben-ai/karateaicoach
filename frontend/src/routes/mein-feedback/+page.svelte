<script>
  import { goto } from "$app/navigation";

  let { data } = $props();
  let trainingsfokusse = $derived(data.trainingsfokusse);

  const statusClass = (status) =>
    status === "AKTIV" ? "badge-status-active" : "badge-status-archived";

  const statusLabel = (status) =>
    status === "AKTIV" ? "Aktiv" : "Inaktiv";

  function planErstellen(fokus) {
    const message = `Erstelle einen Karate-Trainingsplan für mich basierend auf meinem Trainingsfokus:\nSchwerpunkt: ${fokus.schwerpunkt}\nBeschreibung: ${fokus.beschreibung}\n\nBitte erstelle einen konkreten, strukturierten Trainingsplan speziell für Karate.`;
    goto(`/chat?message=${encodeURIComponent(message)}`);
  }
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-bullseye me-2"></i>Mein Feedback</h1>
</div>
<p class="page-subtitle">Trainingsfokus vom Coach einsehen und Trainingsplan erstellen</p>

{#if trainingsfokusse && trainingsfokusse.length > 0}
  <div class="row g-3">
    {#each trainingsfokusse as fokus}
      <div class="col-12">
        <div class="card">
          <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <h5 class="fw-600 mb-1">{fokus.schwerpunkt}</h5>
                <p class="mb-2" style="color: var(--text-muted);">{fokus.beschreibung}</p>
                <span class="status-badge {statusClass(fokus.status)}">
                  {statusLabel(fokus.status)}
                </span>
              </div>
              {#if fokus.status === "AKTIV"}
                <button
                  class="btn btn-primary"
                  onclick={() => planErstellen(fokus)}
                >
                  <i class="bi bi-robot me-2"></i>Plan erstellen
                </button>
              {/if}
            </div>
          </div>
        </div>
      </div>
    {/each}
  </div>
{:else}
  <div class="card">
    <div class="card-body text-center py-5" style="color: var(--text-muted);">
      <i class="bi bi-bullseye" style="font-size: 2rem; display: block; margin-bottom: 0.5rem;"></i>
      Noch kein Feedback vom Coach vorhanden
    </div>
  </div>
{/if}
