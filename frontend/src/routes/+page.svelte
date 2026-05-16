<script>
  import { enhance } from '$app/forms';

  let { data, form } = $props();
  let isAuthenticated = $derived(data.isAuthenticated);
  let user = $derived(data.user);
  let profileIncomplete = $derived(data.profileIncomplete);
  let unreadCount = $derived(data.unreadCount ?? 0);
  let notificationDismissed = $state(false);

  const guertelgrade = ['Weiss', 'Gelb', 'Orange', 'Grün', 'Blau', 'Braun', 'Schwarz'];
</script>

{#if isAuthenticated}
  <div class="hero-section" style="animation: ka-slide-down 0.5s ease both;">
    <div class="hero-icon">
      <i class="bi bi-shield-fill"></i>
    </div>
    <h1 class="hero-title">Willkommen, {user.given_name ?? user.name}!</h1>
    <p class="hero-subtitle">
      Verwalte deine Trainingspläne und verfolge deinen Fortschritt mit KarateAI Coach.
    </p>
  </div>

  {#if unreadCount > 0 && !notificationDismissed}
    <div class="feedback-notification" style="animation: ka-fly-up 0.4s ease both; animation-delay: 0.05s;">
      <div class="feedback-notification-inner">
        <div class="feedback-notification-icon">
          <i class="bi bi-bell-fill"></i>
          <span class="feedback-notification-badge">{unreadCount}</span>
        </div>
        <div class="feedback-notification-body">
          <strong>Neues Feedback vom Coach!</strong>
          <span>
            Du hast {unreadCount === 1 ? 'eine ungelesene Nachricht' : `${unreadCount} ungelesene Nachrichten`} in deinem Feedback.
          </span>
        </div>
        <div class="feedback-notification-actions">
          <a href="/mein-feedback" class="btn btn-primary btn-sm">
            <i class="bi bi-arrow-right me-1"></i>Jetzt ansehen
          </a>
          <button
            class="btn btn-ghost btn-sm"
            onclick={() => (notificationDismissed = true)}
            aria-label="Schliessen"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
      </div>
    </div>
  {/if}

  <section class="features-section">
    <div class="page-content">
      <div class="row g-3 mb-4">
        {#if user.user_roles && user.user_roles.includes("admin")}
          <div class="col-md-4" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.1s;">
            <div class="stat-card">
              <div class="stat-card-icon"><i class="bi bi-people-fill"></i></div>
              <div class="stat-card-title">Sportler</div>
              <div class="stat-card-text">Verwalte deine Athleten</div>
              <a href="/sportler" class="btn btn-outline-primary btn-sm mt-3">
                <i class="bi bi-arrow-right me-1"></i>Öffnen
              </a>
            </div>
          </div>
          <div class="col-md-4" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.22s;">
            <div class="stat-card">
              <div class="stat-card-icon"><i class="bi bi-bullseye"></i></div>
              <div class="stat-card-title">Trainingsfokus</div>
              <div class="stat-card-text">Schwerpunkte für Sportler definieren</div>
              <a href="/trainingsfokus" class="btn btn-outline-primary btn-sm mt-3">
                <i class="bi bi-arrow-right me-1"></i>Öffnen
              </a>
            </div>
          </div>
          <div class="col-md-4" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.34s;">
            <div class="stat-card">
              <div class="stat-card-icon"><i class="bi bi-journal-text"></i></div>
              <div class="stat-card-title">Trainingspläne</div>
              <div class="stat-card-text">Übersicht aller Pläne verwalten</div>
              <a href="/trainingsplan" class="btn btn-outline-primary btn-sm mt-3">
                <i class="bi bi-arrow-right me-1"></i>Öffnen
              </a>
            </div>
          </div>
        {:else}
          <div class="col-md-4" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.1s;">
            <div class="stat-card">
              <div class="stat-card-icon"><i class="bi bi-bullseye"></i></div>
              <div class="stat-card-title">Mein Feedback</div>
              <div class="stat-card-text">Trainingsfokus vom Coach einsehen</div>
              <a href="/mein-feedback" class="btn btn-outline-primary btn-sm mt-3">
                <i class="bi bi-arrow-right me-1"></i>Öffnen
              </a>
            </div>
          </div>
          <div class="col-md-4" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.22s;">
            <div class="stat-card">
              <div class="stat-card-icon"><i class="bi bi-journal-text"></i></div>
              <div class="stat-card-title">Meine Trainingspläne</div>
              <div class="stat-card-text">Aktuelle und vergangene Pläne</div>
              <a href="/meine-trainingsplaene" class="btn btn-outline-primary btn-sm mt-3">
                <i class="bi bi-arrow-right me-1"></i>Öffnen
              </a>
            </div>
          </div>
          <div class="col-md-4" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.34s;">
            <div class="stat-card">
              <div class="stat-card-icon"><i class="bi bi-chat-dots"></i></div>
              <div class="stat-card-title">KI-Assistent</div>
              <div class="stat-card-text">Trainingsplan mit KI erstellen</div>
              <a href="/chat" class="btn btn-outline-primary btn-sm mt-3">
                <i class="bi bi-arrow-right me-1"></i>Öffnen
              </a>
            </div>
          </div>
        {/if}
      </div>
    </div>
  </section>

  {#if profileIncomplete}
    <div class="setup-overlay">
      <div class="setup-card">
        <div class="setup-icon-wrap">
          <i class="bi bi-person-fill-gear setup-icon"></i>
        </div>
        <h2 class="setup-title">Fast fertig, {user.given_name ?? user.name}!</h2>
        <p class="setup-text">
          Damit dein Coach dir den perfekten Trainingsplan erstellen kann,
          brauchen wir noch zwei Angaben von dir.
        </p>

        {#if form?.error}
          <div class="alert alert-danger py-2 mb-3 text-start">
            <i class="bi bi-exclamation-circle me-1"></i>{form.error}
          </div>
        {/if}

        <form method="POST" action="?/completeProfile" use:enhance class="text-start">
          <div class="mb-3">
            <label class="form-label fw-semibold" for="guertelgrad">Gürtelgrad</label>
            <select class="form-select" id="guertelgrad" name="guertelgrad" required>
              <option value="" disabled selected>Gürtelgrad wählen…</option>
              {#each guertelgrade as g}
                <option value={g}>{g}</option>
              {/each}
            </select>
          </div>
          <div class="mb-4">
            <label class="form-label fw-semibold" for="gewicht">Gewicht (kg)</label>
            <input
              class="form-control"
              id="gewicht"
              name="gewicht"
              type="number"
              placeholder="z.B. 70"
              min="1"
              max="300"
              required
            />
          </div>
          <button type="submit" class="btn btn-primary w-100">
            <i class="bi bi-check-lg me-2"></i>Profil abschliessen &amp; loslegen
          </button>
        </form>
      </div>
    </div>
  {/if}

{:else}
  <div class="hero-section" style="animation: ka-slide-down 0.5s ease both;">
    <div class="hero-icon">
      <i class="bi bi-shield-fill"></i>
    </div>
    <h1 class="hero-title">KarateAI Coach</h1>
    <p class="hero-subtitle">
      KI-gestützte Trainingspläne für Karateka. Verwalte Sportler, erstelle personalisierte Pläne und verfolge den Fortschritt.
    </p>
    <div class="d-flex gap-3 justify-content-center">
      <a href="/login" class="btn btn-primary btn-lg">
        <i class="bi bi-box-arrow-in-right me-2"></i>Anmelden
      </a>
      <a href="/signup" class="btn btn-outline-secondary btn-lg">Registrieren</a>
    </div>
  </div>

  <section class="features-section">
    <div class="page-content">
      <div class="row g-3">
        <div class="col-md-4 d-flex" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.1s;">
          <div class="stat-card w-100">
            <div class="stat-card-icon"><i class="bi bi-robot"></i></div>
            <div class="stat-card-title">KI-Trainingspläne</div>
            <div class="stat-card-text">Personalisierte Pläne basierend auf Gürtelgrad und Trainingszielen</div>
          </div>
        </div>
        <div class="col-md-4 d-flex" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.22s;">
          <div class="stat-card w-100">
            <div class="stat-card-icon"><i class="bi bi-graph-up"></i></div>
            <div class="stat-card-title">Fortschritt verfolgen</div>
            <div class="stat-card-text">Dokumentiere Trainingseinheiten und überwache die Entwicklung</div>
          </div>
        </div>
        <div class="col-md-4 d-flex" style="animation: ka-fly-up 0.5s ease both; animation-delay: 0.34s;">
          <div class="stat-card w-100">
            <div class="stat-card-icon"><i class="bi bi-people-fill"></i></div>
            <div class="stat-card-title">Sportlerverwaltung</div>
            <div class="stat-card-text">Betreue mehrere Athleten und behalte den Überblick</div>
          </div>
        </div>
      </div>
    </div>
  </section>
{/if}

<style>
  .setup-overlay {
    position: fixed;
    inset: 0;
    z-index: 2000;
    background: rgba(0, 0, 0, 0.75);
    backdrop-filter: blur(6px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    animation: fadeIn 0.2s ease;
  }

  .setup-card {
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 20px;
    padding: 2.5rem 2rem;
    max-width: 420px;
    width: 100%;
    text-align: center;
    box-shadow: 0 24px 64px rgba(0, 0, 0, 0.5);
    animation: slideUp 0.25s ease;
  }

  .setup-icon-wrap {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: color-mix(in srgb, var(--accent) 15%, transparent);
    border: 2px solid color-mix(in srgb, var(--accent) 40%, transparent);
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1.25rem;
  }

  .setup-icon {
    font-size: 1.75rem;
    color: var(--accent);
  }

  .setup-title {
    font-size: 1.3rem;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 0.5rem;
  }

  .setup-text {
    font-size: 0.9rem;
    color: var(--text-muted);
    margin-bottom: 1.5rem;
    line-height: 1.6;
  }

  @keyframes fadeIn {
    from { opacity: 0; }
    to   { opacity: 1; }
  }

  @keyframes slideUp {
    from { opacity: 0; transform: translateY(16px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  .feedback-notification {
    max-width: 860px;
    margin: 0 auto 1.5rem;
    padding: 0 1rem;
  }

  .feedback-notification-inner {
    display: flex;
    align-items: center;
    gap: 1rem;
    background: color-mix(in srgb, var(--accent) 12%, var(--card-bg));
    border: 1px solid color-mix(in srgb, var(--accent) 40%, transparent);
    border-radius: 14px;
    padding: 1rem 1.25rem;
    box-shadow: 0 4px 20px color-mix(in srgb, var(--accent) 15%, transparent);
  }

  .feedback-notification-icon {
    position: relative;
    flex-shrink: 0;
    font-size: 1.5rem;
    color: var(--accent);
    line-height: 1;
  }

  .feedback-notification-badge {
    position: absolute;
    top: -6px;
    right: -8px;
    background: var(--accent);
    color: #fff;
    font-size: 0.65rem;
    font-weight: 700;
    border-radius: 999px;
    padding: 1px 5px;
    line-height: 1.4;
  }

  .feedback-notification-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.15rem;
    font-size: 0.9rem;
    color: var(--text-primary);
  }

  .feedback-notification-body span {
    color: var(--text-muted);
    font-size: 0.85rem;
  }

  .feedback-notification-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    flex-shrink: 0;
  }

  .btn-ghost {
    background: transparent;
    border: none;
    color: var(--text-muted);
    padding: 0.25rem 0.5rem;
    border-radius: 6px;
    cursor: pointer;
    transition: color 0.15s, background 0.15s;
  }

  .btn-ghost:hover {
    color: var(--text-primary);
    background: color-mix(in srgb, var(--text-muted) 15%, transparent);
  }

  @media (max-width: 576px) {
    .feedback-notification-inner {
      flex-wrap: wrap;
    }

    .feedback-notification-actions {
      width: 100%;
      justify-content: flex-end;
    }
  }
</style>
