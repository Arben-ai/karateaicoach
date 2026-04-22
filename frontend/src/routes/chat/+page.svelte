<script>
  import { enhance } from "$app/forms";
  import { marked } from "marked";

  let { data } = $props();
  let user = $derived(data.user);
  let isAuthenticated = $derived(data.isAuthenticated);

  let message = $state("");
  let messages = $state([
    { type: "bot", text: "Hallo! Wie kann ich dir helfen?" },
  ]);
</script>

{#if isAuthenticated}
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

    <div class="chat-input p-3 border-top">
      <form
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
</style>
