<script>
  import { enhance } from "$app/forms";

  let { data, form } = $props();

  let sportler = $derived(data.sportler);
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