<script>
  import favicon from "$lib/assets/favicon.svg";
  import "./styles.css";

  let { data, children } = $props();
  let user = $derived(data.user);
  let isAuthenticated = $derived(data.isAuthenticated);
</script>

<svelte:head>
  <link rel="icon" href={favicon} />
</svelte:head>

<nav class="navbar navbar-expand-lg">
  <div class="container">
    <a class="navbar-brand" href="/">
      <i class="bi bi-shield-fill brand-icon"></i>KarateAI Coach
    </a>

    <button
      class="navbar-toggler border-0"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#navbarNav"
      aria-controls="navbarNav"
      aria-expanded="false"
      aria-label="Navigation umschalten"
    >
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        {#if isAuthenticated}
          {#if user.user_roles && user.user_roles.includes("admin")}
            <li class="nav-item">
              <a class="nav-link" href="/sportler">
                <i class="bi bi-people me-1"></i>Sportler
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/trainingsfokus">
                <i class="bi bi-bullseye me-1"></i>Trainingsfokus
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/trainingsplan">
                <i class="bi bi-journal-text me-1"></i>Trainingspläne
              </a>
            </li>
          {:else}
            <li class="nav-item">
              <a class="nav-link" href="/mein-feedback">
                <i class="bi bi-bullseye me-1"></i>Mein Feedback
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/meine-trainingsplaene">
                <i class="bi bi-journal-text me-1"></i>Meine Trainingspläne
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/chat">
                <i class="bi bi-chat-dots me-1"></i>Chat
              </a>
            </li>
          {/if}

          <li class="nav-item">
            <a class="nav-link" href="/account">
              <i class="bi bi-person-circle me-1"></i>Account
            </a>
          </li>
        {/if}
      </ul>

      <div class="d-flex align-items-center gap-3">
        {#if isAuthenticated}
          <span class="text-muted small">
            <i class="bi bi-circle-fill text-success me-1" style="font-size: 0.5rem;"></i>
            {user.name}
          </span>
          <form method="POST" action="/logout">
            <button class="btn btn-outline-secondary btn-sm">
              <i class="bi bi-box-arrow-right me-1"></i>Logout
            </button>
          </form>
        {:else}
          <a href="/login" class="btn btn-primary btn-sm">
            <i class="bi bi-box-arrow-in-right me-1"></i>Login
          </a>
          <a href="/signup" class="btn btn-outline-secondary btn-sm">Registrieren</a>
        {/if}
      </div>
    </div>
  </div>
</nav>

<main class="page-main">
  {@render children()}
</main>

<footer class="site-footer">
  <div class="footer-main">
    <div class="footer-grid">

      <!-- Brand column -->
      <div class="footer-col footer-col-brand">
        <div class="footer-brand">
          <i class="bi bi-shield-fill footer-brand-icon"></i>
          KarateAI Coach
        </div>
        <p class="footer-tagline">
          KI-gestützte Trainingspläne für Karateka — personalisiert, effizient und immer dabei.
        </p>
        <p class="footer-copy-small">© 2026 ZHAW School of Management and Law</p>
      </div>

      <!-- Navigation column -->
      <div class="footer-col">
        <h4 class="footer-heading">Navigation</h4>
        <ul class="footer-links">
          <li><a href="/">Startseite</a></li>
          {#if isAuthenticated}
            {#if user.user_roles && user.user_roles.includes("admin")}
              <li><a href="/sportler">Sportler</a></li>
              <li><a href="/trainingsfokus">Trainingsfokus</a></li>
              <li><a href="/trainingsplan">Trainingspläne</a></li>
            {:else}
              <li><a href="/mein-feedback">Mein Feedback</a></li>
              <li><a href="/meine-trainingsplaene">Meine Trainingspläne</a></li>
              <li><a href="/chat">KI-Assistent</a></li>
            {/if}
            <li><a href="/account">Account</a></li>
          {:else}
            <li><a href="/login">Anmelden</a></li>
            <li><a href="/signup">Registrieren</a></li>
          {/if}
        </ul>
      </div>

      <!-- Project column -->
      <div class="footer-col">
        <h4 class="footer-heading">Projekt</h4>
        <ul class="footer-links">
          <li>
            <a href="https://www.zhaw.ch/de/sml/" target="_blank" rel="noopener">
              ZHAW School of Management
            </a>
          </li>
          <li>
            <a href="https://github.com/Arben-ai" target="_blank" rel="noopener">
              GitHub
            </a>
          </li>
          <li><span class="footer-tag">Software Engineering 2</span></li>
        </ul>
      </div>

    </div>
  </div>

  <div class="footer-bottom">
    <div class="footer-bottom-inner">
      <span>Made with <i class="bi bi-heart-fill footer-heart"></i> and AI</span>
      <span class="footer-bottom-divider"></span>
      <span>KarateAI Coach &nbsp;·&nbsp; 2026</span>
    </div>
  </div>
</footer>

<style>
  .site-footer {
    margin-top: 4rem;
    background: var(--bg-secondary);
    border-top: 1px solid var(--border-color);
  }

  .footer-main {
    max-width: 1200px;
    margin: 0 auto;
    padding: 3.5rem 1.5rem 2.5rem;
  }

  .footer-grid {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr;
    gap: 3rem;
  }

  @media (max-width: 768px) {
    .footer-grid {
      grid-template-columns: 1fr;
      gap: 2rem;
    }
  }

  .footer-col-brand {
    padding-right: 2rem;
  }

  .footer-brand {
    font-size: 1.15rem;
    font-weight: 700;
    color: var(--accent);
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.85rem;
  }

  .footer-brand-icon {
    font-size: 1.1rem;
  }

  .footer-tagline {
    font-size: 0.875rem;
    color: var(--text-muted);
    line-height: 1.65;
    margin-bottom: 1rem;
  }

  .footer-copy-small {
    font-size: 0.78rem;
    color: var(--text-muted);
    opacity: 0.6;
    margin: 0;
  }

  .footer-heading {
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--text-muted);
    margin-bottom: 1rem;
  }

  .footer-links {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
  }

  .footer-links a {
    font-size: 0.875rem;
    color: var(--text-muted);
    text-decoration: none;
    transition: color 0.15s;
    display: inline-flex;
    align-items: center;
    gap: 0.3rem;
  }

  .footer-links a:hover {
    color: var(--text-primary);
  }

  .footer-tag {
    font-size: 0.78rem;
    color: var(--text-muted);
    opacity: 0.6;
  }

  .footer-bottom {
    border-top: 1px solid var(--border-color);
    padding: 1rem 1.5rem;
  }

  .footer-bottom-inner {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    font-size: 0.78rem;
    color: var(--text-muted);
    opacity: 0.7;
  }

  .footer-bottom-divider {
    width: 1px;
    height: 12px;
    background: var(--border-color);
  }

  .footer-heart {
    color: var(--accent);
    font-size: 0.7rem;
    vertical-align: middle;
  }
</style>
