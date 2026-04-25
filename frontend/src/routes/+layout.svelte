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

<div class="container mt-4 mb-5">
  {@render children()}
</div>

<footer class="site-footer">
  <div class="container">
    <div class="footer-inner">
      <span class="footer-brand">
        <i class="bi bi-shield-fill me-2"></i>KarateAI Coach
      </span>
      <span class="footer-divider">·</span>
      <span class="footer-copy">© 2026 ZHAW School of Management and Law</span>
      <span class="footer-divider">·</span>
      <span class="footer-copy">Software Engineering 2</span>
    </div>
  </div>
</footer>

<style>
  .site-footer {
    margin-top: auto;
    border-top: 1px solid var(--border-color);
    padding: 1rem 0;
    background: var(--card-bg);
  }
  .footer-inner {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    gap: 0.5rem;
    font-size: 0.8rem;
    color: var(--text-muted);
  }
  .footer-brand {
    color: var(--accent);
    font-weight: 600;
  }
  .footer-divider {
    opacity: 0.4;
  }
</style>
