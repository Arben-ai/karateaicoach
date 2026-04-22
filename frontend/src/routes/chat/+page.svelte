<script>
  import { enhance } from "$app/forms";
  import { untrack } from "svelte";

  let { form } = $props();

  let messages = $state([]);
  let input = $state("");
  let loading = $state(false);

  $effect(() => {
    if (form?.success && form.reply) {
      untrack(() => messages.push({ role: "ai", text: form.reply }));
    } else if (form?.error) {
      untrack(() => messages.push({ role: "error", text: form.error }));
    }
  });

  function handleSubmit() {
    if (!input.trim()) return;
    messages = [...messages, { role: "user", text: input }];
    input = "";
    loading = true;
    return async ({ update }) => {
      await update({ reset: false });
      loading = false;
    };
  }
</script>

<div class="d-flex justify-content-between align-items-center mb-1">
  <h1 class="page-title"><i class="bi bi-chat-dots me-2"></i>KI-Assistent</h1>
</div>
<p class="page-subtitle">Frage den Assistenten nach Sportlern und Trainingsplänen oder lass ihn neue erstellen.</p>

<div class="card mb-3">
  <div class="card-body p-0">
    <div
      class="chat-window p-3"
      style="min-height: 400px; max-height: 500px; overflow-y: auto; display: flex; flex-direction: column; gap: 0.75rem;"
    >
      {#if messages.length === 0}
        <div class="text-center py-5" style="color: var(--text-muted);">
          <i class="bi bi-chat-square-dots" style="font-size: 2.5rem; display: block; margin-bottom: 0.75rem;"></i>
          Stelle eine Frage oder gib einen Auftrag, z.B.:<br />
          <em>„Zeige alle Sportler"</em> oder <em>„Erstelle einen Trainingsplan für Max"</em>
        </div>
      {/if}

      {#each messages as msg}
        {#if msg.role === "user"}
          <div class="d-flex justify-content-end">
            <div
              class="px-3 py-2 rounded-3"
              style="background: var(--accent-primary); color: white; max-width: 75%; white-space: pre-wrap;"
            >
              {msg.text}
            </div>
          </div>
        {:else if msg.role === "ai"}
          <div class="d-flex justify-content-start">
            <div
              class="px-3 py-2 rounded-3"
              style="background: var(--card-bg); border: 1px solid var(--border-color); max-width: 75%; white-space: pre-wrap; color: var(--text-primary);"
            >
              <i class="bi bi-robot me-1" style="color: var(--accent-primary);"></i>{msg.text}
            </div>
          </div>
        {:else}
          <div class="alert alert-danger py-2 mb-0">{msg.text}</div>
        {/if}
      {/each}

      {#if loading}
        <div class="d-flex justify-content-start">
          <div
            class="px-3 py-2 rounded-3"
            style="background: var(--card-bg); border: 1px solid var(--border-color); color: var(--text-muted);"
          >
            <span class="spinner-border spinner-border-sm me-2" role="status"></span>Denkt nach…
          </div>
        </div>
      {/if}
    </div>
  </div>
</div>

<form method="POST" action="?/send" use:enhance={handleSubmit}>
  <div class="input-group">
    <input
      class="form-control"
      name="message"
      type="text"
      placeholder="Nachricht eingeben…"
      bind:value={input}
      disabled={loading}
      autocomplete="off"
    />
    <button class="btn btn-primary" type="submit" disabled={loading || !input.trim()}>
      <i class="bi bi-send me-1"></i>Senden
    </button>
  </div>
</form>
