<script>
  import { enhance } from "$app/forms";
  import { marked } from "marked";
  import { onMount } from "svelte";

  let { data } = $props();
  let user = $derived(data.user);
  let isAuthenticated = $derived(data.isAuthenticated);

  let initialMessage = $derived(data.initialMessage);
  let mode = $derived(data.mode);
  let fokusId = $derived(data.fokusId);
  let schwerpunkt = $derived(data.schwerpunkt);

  let message = $state("");
  let messages = $state([
    { type: "bot", text: "Hallo! Wie kann ich dir helfen?" },
  ]);
  let formRef = $state(null);
  let generatingPlan = $state(false);
  let savedPlanId = $state(null);

  onMount(() => {
    if (initialMessage) {
      message = initialMessage;
      setTimeout(() => formRef?.requestSubmit(), 100);
    }
  });
</script>

{#if isAuthenticated}
  <div class="page-content">
  <div class="d-flex justify-content-between align-items-center mb-1">
    <h1 class="page-title"><i class="bi bi-chat-dots me-2"></i>KI-Assistent</h1>
  </div>
  <p class="page-subtitle">Frage den Assistenten nach Sportlern und Trainingsplänen oder lass ihn neue erstellen.</p>

  <div class="chat-wrapper d-flex flex-column card">
    <div class="chat-messages flex-grow-1 overflow-auto p-3 d-flex flex-column">
      {#each messages as msg}
        {#if msg.type === "loading"}
          <div class="message bot align-self-start typing">
            <span></span><span></span><span></span>
          </div>
        {:else}
          <div class="message {msg.type} align-self-{msg.type === 'user' ? 'end' : 'start'}">
            {#if msg.type === "bot"}
              {@html marked(msg.text)}
            {:else}
              {msg.text}
            {/if}
          </div>
        {/if}
      {/each}
    </div>

    {#if mode === "plan" && fokusId}
      <div class="save-plan-bar border-top p-3">
        <div class="d-flex align-items-center gap-3 flex-wrap">
          {#if savedPlanId}
            <div class="flex-grow-1" style="font-size: 0.9rem; color: #15803d;">
              <i class="bi bi-check-circle-fill me-2"></i>Trainingsplan wurde gespeichert.
            </div>
            <a href="/meine-trainingsplaene?planId={savedPlanId}" class="btn btn-outline-success btn-sm">
              <i class="bi bi-journal-text me-1"></i>Zum Trainingsplan
            </a>
          {:else}
            <div class="flex-grow-1" style="font-size: 0.9rem; color: var(--text-muted);">
              <i class="bi bi-robot me-2" style="color: var(--accent);"></i>
              Trainingsplan für <strong>{schwerpunkt}</strong>
              {#if generatingPlan}
                — <em>KI generiert deinen Plan, bitte warten (ca. 30–60 Sek.)…</em>
              {:else}
                — Passt der Vorschlag? Klicke um den Plan zu speichern.
              {/if}
            </div>
            <form
              method="POST"
              action="?/savePlan"
              use:enhance={() => {
                generatingPlan = true;
                return async ({ result }) => {
                  generatingPlan = false;
                  if (result.type === "success" && result.data?.planSaved) {
                    savedPlanId = result.data.planId;
                    const text = result.data.planTitel
                      ? `Dein Trainingsplan **${result.data.planTitel}** wurde gespeichert!`
                      : "Dein Trainingsplan wurde gespeichert!";
                    messages.push({ type: "bot", text });
                  } else if (result.data?.error) {
                    messages.push({ type: "bot", text: `Fehler: ${result.data.error}` });
                  }
                };
              }}
            >
              <input type="hidden" name="fokusId" value={fokusId} />
              <button type="submit" class="btn btn-success" disabled={generatingPlan}>
                {#if generatingPlan}
                  <span class="spinner-border spinner-border-sm me-2"></span>Generiert…
                {:else}
                  <i class="bi bi-journal-check me-2"></i>Trainingsplan verwenden
                {/if}
              </button>
            </form>
          {/if}
        </div>
      </div>
    {/if}

    <div class="chat-input p-3 border-top">
      <form
        bind:this={formRef}
        method="POST"
        action="?/send"
        class="d-flex gap-2"
        use:enhance={({ formData }) => {
          const text = formData.get("message");
          messages.push({ type: "user", text });
          message = "";
          messages.push({ type: "loading" });

          return async ({ result }) => {
            messages = messages.filter((m) => m.type !== "loading");
            if (result.type === "success" && result.data?.reply) {
              messages.push({ type: "bot", text: result.data.reply });
            } else {
              messages.push({ type: "bot", text: "Fehler beim Serverkontakt." });
            }
          };
        }}
      >
        <textarea
          name="message"
          class="form-control"
          placeholder="Nachricht eingeben…"
          bind:value={message}
          required
          rows="1"
        ></textarea>
        <button type="submit" class="btn btn-primary align-self-end">
          <i class="bi bi-send me-1"></i>Senden
        </button>
      </form>
    </div>
  </div>
  </div><!-- /page-content -->
{/if}

<style>
  .chat-wrapper {
    height: 75vh;
    overflow: hidden;
  }

  .chat-messages {
    gap: 0.75rem;
    overflow-y: auto;
  }

  .message {
    padding: 0.5rem 1rem;
    border-radius: 10px;
    max-width: 75%;
    white-space: pre-wrap;
  }

  .message.bot {
    background-color: var(--card-bg, #3d3d43);
    border: 1px solid var(--border-color, #4e4e53);
    color: var(--text-primary, #f5f5f5);
  }

  .message.user {
    background-color: var(--accent-primary, #e63946);
    color: white;
  }

  .message.typing span {
    height: 8px;
    width: 8px;
    margin: 0 3px;
    display: inline-block;
    background: var(--text-muted, #a8a8a8);
    border-radius: 50%;
    animation: blink 1.4s infinite both;
  }

  .message.typing span:nth-child(2) { animation-delay: 0.2s; }
  .message.typing span:nth-child(3) { animation-delay: 0.4s; }

  @keyframes blink {
    0%   { opacity: 0.2; }
    20%  { opacity: 1; }
    100% { opacity: 0.2; }
  }

  textarea { min-height: 42px; resize: none; }

  .save-plan-bar {
    background: rgba(40, 167, 69, 0.06);
    border-color: rgba(40, 167, 69, 0.2) !important;
  }
</style>
