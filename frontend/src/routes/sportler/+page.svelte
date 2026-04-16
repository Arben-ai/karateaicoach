<script>
  import { enhance } from "$app/forms";

  let { data, form } = $props();

  let sportler = $derived(data.sportler);
  let pagination = $derived(data.pagination);

  const createPageLink = (page) => `/sportler?page=${page}&size=${pagination.size}`;
</script>

<h1 class="mt-3">Create Sportler</h1>

{#if form?.success}
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    Sportler created successfully!
  </div>
{/if}

{#if form?.error}
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    {form.error}
  </div>
{/if}

<form class="mb-5" method="POST" action="?/createSportler" use:enhance>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="name">Name</label>
      <input
        class="form-control"
        id="name"
        name="name"
        type="text"
        required
      />
    </div>
  </div>

  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="email">Email</label>
      <input
        class="form-control"
        id="email"
        name="email"
        type="email"
        required
      />
    </div>
  </div>

  <button type="submit" class="btn btn-primary">Submit</button>
</form>

<h1>All Sportler</h1>

<table class="table">
  <thead>
    <tr>
      <th scope="col">Name</th>
      <th scope="col">Email</th>
      <th scope="col">ID</th>
    </tr>
  </thead>
  <tbody>
    {#each sportler as s}
      <tr>
        <td>{s.name}</td>
        <td>{s.email}</td>
        <td>{s.id}</td>
      </tr>
    {/each}
  </tbody>
</table>

{#if pagination.totalPages > 0}
  <div class="d-flex justify-content-between align-items-center mt-3">
    <p class="mb-0">
      Seite {pagination.page + 1} von {pagination.totalPages}
      ({pagination.totalElements} Eintraege)
    </p>

    <div class="d-flex gap-2">
      <a
        class="btn btn-outline-secondary"
        class:disabled={pagination.page === 0}
        href={pagination.page === 0 ? "#" : createPageLink(pagination.page - 1)}
        aria-disabled={pagination.page === 0}
      >
        Zurueck
      </a>

      <a
        class="btn btn-outline-secondary"
        class:disabled={pagination.page >= pagination.totalPages - 1}
        href={pagination.page >= pagination.totalPages - 1 ? "#" : createPageLink(pagination.page + 1)}
        aria-disabled={pagination.page >= pagination.totalPages - 1}
      >
        Weiter
      </a>
    </div>
  </div>
{/if}
