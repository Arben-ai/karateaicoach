<script>
  let { data } = $props();
  let { isAuthenticated } = data;
</script>

{#if isAuthenticated}
  <h1>Willkommen beim KarateAI Coach!</h1>

  <p>Deine Trainingspläne und Fortschritte stehen bereit.</p>

{:else}
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">KarateAI Coach</div>

          <div class="card-body text-center">
            <h5 class="card-title">Get Started</h5>

            <p class="card-text">
              Bitte logge dich ein oder registriere dich, um deine Trainingspläne zu verwalten.
            </p>

            <a href="/login" class="btn btn-primary me-2">Login</a>
            <a href="/signup" class="btn btn-outline-primary">Sign Up</a>
          </div>

        </div>
      </div>
    </div>
  </div>
{/if}