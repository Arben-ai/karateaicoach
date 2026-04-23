<script>
  let { data } = $props();
  let isAuthenticated = $derived(data.isAuthenticated);
  let user = $derived(data.user);
</script>

{#if isAuthenticated}
  <div class="hero-section">
    <div class="hero-icon">
      <i class="bi bi-shield-fill"></i>
    </div>
    <h1 class="hero-title">Willkommen, {user.given_name ?? user.name}!</h1>
    <p class="hero-subtitle">
      Verwalte deine Trainingspläne und verfolge deinen Fortschritt mit KarateAI Coach.
    </p>
  </div>

  <div class="row g-3 mb-4">
    {#if user.user_roles && user.user_roles.includes("admin")}
      <div class="col-md-4">
        <div class="stat-card">
          <div class="stat-card-icon"><i class="bi bi-people-fill"></i></div>
          <div class="stat-card-title">Sportler</div>
          <div class="stat-card-text">Verwalte deine Athleten</div>
          <a href="/sportler" class="btn btn-outline-primary btn-sm mt-3">
            <i class="bi bi-arrow-right me-1"></i>Öffnen
          </a>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card">
          <div class="stat-card-icon"><i class="bi bi-bullseye"></i></div>
          <div class="stat-card-title">Trainingsfokus</div>
          <div class="stat-card-text">Schwerpunkte für Sportler definieren</div>
          <a href="/trainingsfokus" class="btn btn-outline-primary btn-sm mt-3">
            <i class="bi bi-arrow-right me-1"></i>Öffnen
          </a>
        </div>
      </div>
      <div class="col-md-4">
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
      <div class="col-md-4">
        <div class="stat-card">
          <div class="stat-card-icon"><i class="bi bi-bullseye"></i></div>
          <div class="stat-card-title">Mein Feedback</div>
          <div class="stat-card-text">Trainingsfokus vom Coach einsehen</div>
          <a href="/mein-feedback" class="btn btn-outline-primary btn-sm mt-3">
            <i class="bi bi-arrow-right me-1"></i>Öffnen
          </a>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card">
          <div class="stat-card-icon"><i class="bi bi-journal-text"></i></div>
          <div class="stat-card-title">Meine Trainingspläne</div>
          <div class="stat-card-text">Aktuelle und vergangene Pläne</div>
          <a href="/meine-trainingsplaene" class="btn btn-outline-primary btn-sm mt-3">
            <i class="bi bi-arrow-right me-1"></i>Öffnen
          </a>
        </div>
      </div>
      <div class="col-md-4">
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

    <div class="col-md-4">
      <div class="stat-card">
        <div class="stat-card-icon"><i class="bi bi-person-circle"></i></div>
        <div class="stat-card-title">Mein Account</div>
        <div class="stat-card-text">Profil und Einstellungen</div>
        <a href="/account" class="btn btn-outline-primary btn-sm mt-3">
          <i class="bi bi-arrow-right me-1"></i>Öffnen
        </a>
      </div>
    </div>
  </div>

{:else}
  <div class="hero-section">
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

  <div class="row g-3 mt-2">
    <div class="col-md-4 d-flex">
      <div class="stat-card w-100">
        <div class="stat-card-icon"><i class="bi bi-robot"></i></div>
        <div class="stat-card-title">KI-Trainingspläne</div>
        <div class="stat-card-text">Personalisierte Pläne basierend auf Gürtelgrad und Trainingszielen</div>
      </div>
    </div>
    <div class="col-md-4 d-flex">
      <div class="stat-card w-100">
        <div class="stat-card-icon"><i class="bi bi-graph-up"></i></div>
        <div class="stat-card-title">Fortschritt verfolgen</div>
        <div class="stat-card-text">Dokumentiere Trainingseinheiten und überwache die Entwicklung</div>
      </div>
    </div>
    <div class="col-md-4 d-flex">
      <div class="stat-card w-100">
        <div class="stat-card-icon"><i class="bi bi-people-fill"></i></div>
        <div class="stat-card-title">Sportlerverwaltung</div>
        <div class="stat-card-text">Betreue mehrere Athleten und behalte den Überblick</div>
      </div>
    </div>
  </div>
{/if}
