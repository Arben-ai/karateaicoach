<script>
  import { enhance } from "$app/forms";

  let { data, form } = $props();

  let sportler = $derived(data.sportler);
  let trainingsplaene = $derived(data.trainingsplaene);
</script>

<h1 class="mt-3">Create Trainingsplan</h1>

{#if form?.success}
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    Trainingsplan created successfully!
  </div>
{/if}

{#if form?.error}
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    {form.error}
  </div>
{/if}

<form class="mb-5" method="POST" action="?/createTrainingsplan" use:enhance>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="titel">Titel</label>
      <input
        class="form-control"
        id="titel"
        name="titel"
        type="text"
        required
      />
    </div>
  </div>

  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="dauer">Dauer (Minuten)</label>
      <input
        class="form-control"
        id="dauer"
        name="dauer"
        type="number"
        min="1"
        required
      />
    </div>

    <div class="col">
      <label class="form-label" for="status">Status</label>
      <select class="form-select" id="status" name="status" required>
        <option value="">Select status...</option>
        <option value="DRAFT">DRAFT</option>
        <option value="ACTIVE">ACTIVE</option>
        <option value="COMPLETED">COMPLETED</option>
        <option value="ARCHIVED">ARCHIVED</option>
      </select>
    </div>

    <div class="col">
      <label class="form-label" for="sportler">Sportler</label>
      <select class="form-select" id="sportler" name="sportlerId" required>
        <option value="">Select sportler...</option>
        {#each sportler as s}
          <option value={s.id}>{s.name}</option>
        {/each}
      </select>
    </div>
  </div>

  <button type="submit" class="btn btn-primary">Submit</button>
</form>

<h1>All Trainingspläne</h1>

<table class="table">
  <thead>
    <tr>
      <th scope="col">Titel</th>
      <th scope="col">Dauer</th>
      <th scope="col">Status</th>
      <th scope="col">SportlerId</th>
    </tr>
  </thead>
  <tbody>
    {#each trainingsplaene as tp}
      <tr>
        <td>{tp.titel}</td>
        <td>{tp.dauer}</td>
        <td>{tp.status}</td>
        <td>{tp.sportlerId}</td>
      </tr>
    {/each}
  </tbody>
</table>