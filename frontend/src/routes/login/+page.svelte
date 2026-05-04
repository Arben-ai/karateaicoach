<script>
  let { form } = $props();
</script>

<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-5" style="animation: ka-scale-in 0.4s ease both;">
      <div class="text-center mb-4">
        <i class="bi bi-shield-fill" style="font-size: 2.5rem; color: var(--accent);"></i>
        <h1 class="h4 mt-2 fw-700">KarateAI Coach</h1>
        <p style="color: var(--text-muted);">Melde dich an, um fortzufahren</p>
      </div>

      <div class="card">
        <div class="card-header">
          <i class="bi bi-box-arrow-in-right me-2"></i>Anmelden
        </div>
        <div class="card-body p-4">
          {#if form?.error}
            <div class="alert alert-danger" role="alert">
              <i class="bi bi-exclamation-circle me-2"></i>{form.error}
            </div>
          {/if}
          <form method="POST" class="needs-validation" novalidate>
            <div class="mb-3">
              <label for="email" class="form-label">E-Mail</label>
              <input type="email" class="form-control" id="email" name="email" placeholder="name@beispiel.ch" required />
            </div>
            <div class="mb-4">
              <label for="password" class="form-label">Passwort</label>
              <input type="password" class="form-control" id="password" name="password" placeholder="••••••••" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">
              <i class="bi bi-box-arrow-in-right me-2"></i>Anmelden
            </button>
          </form>
        </div>
      </div>

      <p class="text-center mt-3" style="color: var(--text-muted); font-size: 0.9rem;">
        Noch kein Konto? <a href="/signup" style="color: var(--accent);">Registrieren</a>
      </p>
    </div>
  </div>
</div>
