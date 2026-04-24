<script>
  import { enhance } from '$app/forms';

  let { data, form } = $props();
  let isAuthenticated = $derived(data.isAuthenticated);

  let signupFormElement;
</script>

<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-5">
      <div class="text-center mb-4">
        <i class="bi bi-shield-fill" style="font-size: 2.5rem; color: var(--accent);"></i>
        <h1 class="h4 mt-2 fw-700">KarateAI Coach</h1>
        <p style="color: var(--text-muted);">Erstelle dein Konto</p>
      </div>

      <div class="card">
        <div class="card-header">
          <i class="bi bi-person-plus me-2"></i>Registrieren
        </div>
        <div class="card-body p-4">
          {#if form?.error}
            <div class="alert alert-danger" role="alert">
              <i class="bi bi-exclamation-circle me-2"></i>{form.error}
            </div>
          {/if}
          <form
            method="POST"
            action="?/signup"
            bind:this={signupFormElement}
            class="needs-validation"
            use:enhance
            novalidate
          >
            <div class="row g-3 mb-3">
              <div class="col">
                <label for="firstName" class="form-label">Vorname</label>
                <input type="text" class="form-control" id="firstName" name="firstName" placeholder="Max" />
              </div>
              <div class="col">
                <label for="lastName" class="form-label">Nachname</label>
                <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Mustermann" />
              </div>
            </div>
            <div class="mb-3">
              <label for="email" class="form-label">E-Mail</label>
              <input type="email" class="form-control" id="email" name="email" placeholder="name@beispiel.ch" required />
            </div>
            <div class="row g-3 mb-3">
              <div class="col-md-8">
                <label for="guertelgrad" class="form-label">Gürtelgrad</label>
                <select class="form-select" id="guertelgrad" name="guertelgrad">
                  <option value="">Kein Gürtelgrad</option>
                  <option value="Weiss">Weiss</option>
                  <option value="Gelb">Gelb</option>
                  <option value="Orange">Orange</option>
                  <option value="Grün">Grün</option>
                  <option value="Blau">Blau</option>
                  <option value="Braun">Braun</option>
                  <option value="Schwarz">Schwarz</option>
                </select>
              </div>
              <div class="col-md-4">
                <label for="gewicht" class="form-label">Gewicht (kg)</label>
                <input type="number" class="form-control" id="gewicht" name="gewicht" placeholder="70" min="0" />
              </div>
            </div>
            <div class="mb-4">
              <label for="password" class="form-label">Passwort</label>
              <input type="password" class="form-control" id="password" name="password" placeholder="••••••••" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">
              <i class="bi bi-person-plus me-2"></i>Konto erstellen
            </button>
          </form>
        </div>
      </div>

      <p class="text-center mt-3" style="color: var(--text-muted); font-size: 0.9rem;">
        Bereits ein Konto? <a href="/login" style="color: var(--accent);">Anmelden</a>
      </p>
    </div>
  </div>
</div>
