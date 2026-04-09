<script>
  import favicon from "$lib/assets/favicon.svg";
  import "./styles.css";

  let { data, children } = $props();
  let { user, isAuthenticated } = data;
</script>

<svelte:head>
  <link rel="icon" href={favicon} />
</svelte:head>

<nav class="navbar navbar-expand-lg bg-light">
  <div class="container-fluid">
    <a class="navbar-brand" href="/">KarateAI Coach</a>

    <button
      class="navbar-toggler"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#navbarNav"
    >
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
      
      <!-- LINKS (Navigation) -->
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        {#if isAuthenticated}
          <li class="nav-item">
            <a class="nav-link" href="/sportler">Sportler</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="/trainingsplan">Trainingspläne</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="/account">Account</a> <!-- ✅ NEU -->
          </li>
        {/if}
      </ul>

      <!-- RECHTS (Login / Logout) -->
      <div class="d-flex align-items-center">
        {#if isAuthenticated}
          <span class="me-2">{user.name}</span>

          <form method="POST" action="/logout">
            <button class="btn btn-primary">Logout</button>
          </form>
        {:else}
          <a href="/login" class="btn btn-primary me-2">Login</a>
          <a href="/signup" class="btn btn-outline-primary">Sign Up</a>
        {/if}
      </div>

    </div>
  </div>
</nav>

<div class="container mt-3">
  {@render children()}
</div>