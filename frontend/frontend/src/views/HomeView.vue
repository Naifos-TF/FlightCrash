<template>
  <div class="appShell">
    <!-- NAVBAR -->
    <header class="nav">
      <div class="navLeft">
        <div class="logo">✈︎</div>
        <div class="brand">
          <div class="brandName">FlightCrash</div>
          <div class="brandTag">Encyclopédie des crashs</div>
        </div>
      </div>

      <nav class="navRight">
        <button class="navLink navLinkActive" @click="goHome">Home</button>
        <span class="pill" :class="apiOk ? 'pill-ok' : 'pill-bad'">API: {{ apiOk ? "OK" : "OFF" }}</span>
      </nav>
    </header>

    <!-- MAIN FULL PAGE -->
    <main class="main">
      <!-- LEFT: Search + Filters + Results -->
      <aside class="leftPane">
        <div class="searchBlock">
          <h1 class="pageTitle">Rechercher un crash</h1>
          <p class="pageSub">Compagnie, lieu, date, type d’avion, numéro de vol…</p>

          <div class="searchRow">
            <input
              v-model="q"
              class="searchInput"
              type="search"
              placeholder="Ex: Air France, Tenerife, 2009, A320…"
              @keydown.enter="search"
            />
            <button class="primaryBtn" @click="search" :disabled="loading">
              {{ loading ? "Recherche…" : "Rechercher" }}
            </button>
          </div>

          <!-- Filters -->
          <div class="filtersGrid">
            <div class="filter">
              <label class="filterLabel">Année</label>
              <select v-model="filters.year" class="filterSelect" @change="search">
                <option value="">Toutes</option>
                <option v-for="y in options.years" :key="y" :value="String(y)">{{ y }}</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Survivors</label>
              <select v-model="filters.survivors" class="filterSelect" @change="search">
                <option value="">Tous</option>
                <option value="true">Oui</option>
                <option value="false">Non</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Country</label>
              <select v-model="filters.country" class="filterSelect" @change="search">
                <option value="">Tous</option>
                <option v-for="c in options.countries" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Region</label>
              <select v-model="filters.region" class="filterSelect" @change="search">
                <option value="">Toutes</option>
                <option v-for="r in options.regions" :key="r" :value="r">{{ r }}</option>
              </select>
            </div>

            <div class="filter filterSpan2">
              <label class="filterLabel">Crash cause</label>
              <select v-model="filters.crashCause" class="filterSelect" @change="search">
                <option value="">Toutes</option>
                <option v-for="cc in options.crashCauses" :key="cc" :value="cc">{{ cc }}</option>
              </select>
            </div>
          </div>

          <div class="searchActions">
            <button class="ghostBtn" @click="reset" :disabled="loading">
              Réinitialiser
            </button>
          </div>
        </div>

        <div class="resultsBlock">
          <div class="resultsHeader">
            <h2 class="sectionTitle">Résultats</h2>
            <span class="count">{{ results.length }}</span>
          </div>

          <div v-if="!results.length && !loading" class="empty">
            Aucun résultat. Lance une recherche.
          </div>

          <div v-if="loading" class="empty">Chargement…</div>

          <ul v-else class="list">
            <li v-for="item in results" :key="item.id">
              <button
                class="resultItem"
                :class="selected?.id === item.id ? 'resultItemActive' : ''"
                @click="select(item)"
              >
                <div class="resultTop">
                  <span class="resultTitle">{{ displayTitle(item) }}</span>
                  <span class="badge">{{ yearOf(item) ?? "—" }}</span>
                </div>
                <div class="resultMeta">
                  <span>{{ item.crashLocation ?? "—" }}</span>
                  <span class="dot">•</span>
                  <span>{{ item.operator ?? "—" }}</span>
                </div>
              </button>
            </li>
          </ul>
        </div>
      </aside>

      <!-- RIGHT: Details -->
      <section class="rightPane">
        <div class="detailsHeader">
          <h2 class="sectionTitle">Fiche crash</h2>
          <div class="detailsHeaderActions" v-if="selected">
            <button class="ghostBtn" @click="copyId(String(selected.id))">Copier l’ID</button>
            <button class="primaryBtn" @click="openDetails(selected.id)">Ouvrir détail</button>
          </div>
        </div>

        <div v-if="!selected" class="empty detailsEmpty">
          Sélectionne un résultat pour afficher les détails.
        </div>

        <div v-else class="detailsCard">
          <div class="detailsTitleRow">
            <div>
              <h3 class="detailsTitle">{{ displayTitle(selected) }}</h3>
              <p class="detailsSub">
                {{ selected.crashDate ?? "—" }} — {{ selected.crashLocation ?? "—" }}
              </p>
            </div>
            <div class="severity">
              <span class="badge badgeDark">
                Fatalities: {{ selected.totalFatalities ?? "—" }}
              </span>
            </div>
          </div>

          <div class="kvGrid">
            <div class="kv">
              <div class="k">Opérateur</div>
              <div class="v">{{ selected.operator ?? "—" }}</div>
            </div>
            <div class="kv">
              <div class="k">Type avion</div>
              <div class="v">{{ selected.aircraft ?? "—" }}</div>
            </div>
            <div class="kv">
              <div class="k">Vol</div>
              <div class="v">{{ selected.flightNo ?? "—" }}</div>
            </div>
            <div class="kv">
              <div class="k">Occupants</div>
              <div class="v">{{ selected.occupants ?? "—" }}</div>
            </div>
            <div class="kv">
              <div class="k">Country / Region</div>
              <div class="v">{{ (selected.country ?? "—") + " / " + (selected.region ?? "—") }}</div>
            </div>
            <div class="kv">
              <div class="k">Survivors</div>
              <div class="v">{{ selected.survivors === true ? "Oui" : selected.survivors === false ? "Non" : "—" }}</div>
            </div>
            <div class="kv filterSpan2">
              <div class="k">Crash cause</div>
              <div class="v">{{ selected.crashCause ?? "—" }}</div>
            </div>
          </div>

          <div class="divider"></div>

          <div class="summary">
            <div class="k">Circonstances</div>
            <p class="v">{{ selected.circumstances ?? "—" }}</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import "@/assets/home.css";

const router = useRouter();

const q = ref("");
const loading = ref(false);
const apiOk = ref(false);

const results = ref([]);
const selected = ref(null);

const filters = ref({
  year: "",
  survivors: "",
  country: "",
  region: "",
  crashCause: "",
});

const options = ref({
  years: [],
  countries: [],
  regions: [],
  crashCauses: [],
});

function displayTitle(item) {
  const op = item?.operator?.trim();
  const ac = item?.aircraft?.trim();
  if (op && ac) return `${op} — ${ac}`;
  return op || ac || `Crash #${item?.id ?? "—"}`;
}

function yearOf(item) {
  const d = item?.crashDate;
  if (!d || typeof d !== "string") return null;
  // "YYYY-MM-DD"
  return d.slice(0, 4);
}

async function pingApi() {
  try {
    const res = await fetch("/api/health");
    apiOk.value = res.ok;
  } catch {
    apiOk.value = false;
  }
}

function buildSearchParams() {
  const p = new URLSearchParams();
  if (q.value.trim()) p.set("q", q.value.trim());

  if (filters.value.year) p.set("year", filters.value.year);
  if (filters.value.survivors) p.set("survivors", filters.value.survivors);
  if (filters.value.country) p.set("country", filters.value.country);
  if (filters.value.region) p.set("region", filters.value.region);
  if (filters.value.crashCause) p.set("crashCause", filters.value.crashCause);

  p.set("limit", "50");
  return p;
}

async function search() {
  loading.value = true;
  selected.value = null;

  try {
    const params = buildSearchParams();
    const res = await fetch(`/api/crashes/search?${params.toString()}`);
    if (!res.ok) throw new Error(`search failed: ${res.status}`);
    const data = await res.json();

    results.value = Array.isArray(data) ? data : [];
    if (results.value.length) selected.value = results.value[0];
  } finally {
    loading.value = false;
  }
}

function select(item) {
  selected.value = item;
}

function reset() {
  q.value = "";
  filters.value = { year: "", survivors: "", country: "", region: "", crashCause: "" };
  results.value = [];
  selected.value = null;
}

function openDetails(id) {
  router.push({ name: "crash-details", params: { id } });
}

function goHome() {
  router.push({ name: "home" });
}

async function copyId(id) {
  try {
    await navigator.clipboard.writeText(id);
  } catch {
    prompt("Copy this ID:", id);
  }
}

/**
 * Charge les listes déroulantes.
 * Recommandé: backend /api/crashes/facets.
 * Fallback: prend un échantillon via search sans q (limit 500) et dérive les valeurs distinctes.
 */
async function loadFilterOptions() {
  // 1) facets endpoint (si tu l’implémentes côté backend)
  try {
    const res = await fetch("/api/crashes/facets");
    if (res.ok) {
      const f = await res.json();
      options.value.years = f.years ?? [];
      options.value.countries = f.countries ?? [];
      options.value.regions = f.regions ?? [];
      options.value.crashCauses = f.crashCauses ?? [];
      return;
    }
  } catch {
    // ignore
  }

  // 2) fallback: sample search depuis la DB
  try {
    const res = await fetch(`/api/crashes/search?limit=500`);
    if (!res.ok) return;
    const data = await res.json();
    if (!Array.isArray(data)) return;

    const years = new Set();
    const countries = new Set();
    const regions = new Set();
    const causes = new Set();

    for (const c of data) {
      const y = yearOf(c);
      if (y) years.add(Number(y));
      if (c.country) countries.add(c.country);
      if (c.region) regions.add(c.region);
      if (c.crashCause) causes.add(c.crashCause);
    }

    options.value.years = Array.from(years).sort((a, b) => b - a);
    options.value.countries = Array.from(countries).sort();
    options.value.regions = Array.from(regions).sort();
    options.value.crashCauses = Array.from(causes).sort();
  } catch {
    // ignore
  }
}

onMounted(async () => {
  await pingApi();
  await loadFilterOptions();
  await search(); // affiche un premier résultat (ou vide) selon ton backend
});
</script>
