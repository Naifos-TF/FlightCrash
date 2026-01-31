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
      <!-- LEFT: Search + Results -->
      <aside class="leftPane">
        <div class="searchBlock">
          <h1 class="pageTitle">Rechercher un crash</h1>
          <p class="pageSub">Compagnie, lieu, date, type d’avion, numéro de vol…</p>

          <div class="searchRow">
            <input
              v-model="q"
              class="searchInput"
              type="search"
              placeholder="Ex: Air France 447, Tenerife, 2009, A320…"
              @keydown.enter="search"
            />
            <button class="primaryBtn" @click="search" :disabled="loading || !q.trim()">
              {{ loading ? "Recherche…" : "Rechercher" }}
            </button>
          </div>

          <div class="searchActions">
            <button class="ghostBtn" @click="reset" :disabled="loading && results.length === 0">
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

          <ul v-else class="list">
            <li v-for="item in results" :key="item.id">
              <button
                class="resultItem"
                :class="selected?.id === item.id ? 'resultItemActive' : ''"
                @click="select(item)"
              >
                <div class="resultTop">
                  <span class="resultTitle">{{ item.title }}</span>
                  <span class="badge">{{ item.year }}</span>
                </div>
                <div class="resultMeta">
                  <span>{{ item.location }}</span>
                  <span class="dot">•</span>
                  <span>{{ item.operator }}</span>
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
            <button class="ghostBtn" @click="copyId(selected.id)">Copier l’ID</button>
            <button class="primaryBtn" @click="openDetails(selected.id)">Ouvrir détail</button>
          </div>
        </div>

        <div v-if="!selected" class="empty detailsEmpty">
          Sélectionne un résultat pour afficher les détails.
        </div>

        <div v-else class="detailsCard">
          <div class="detailsTitleRow">
            <div>
              <h3 class="detailsTitle">{{ selected.title }}</h3>
              <p class="detailsSub">{{ selected.date }} — {{ selected.location }}</p>
            </div>
            <div class="severity">
              <span class="badge badgeDark">Fatalities: {{ selected.fatalities }}</span>
            </div>
          </div>

          <div class="kvGrid">
            <div class="kv">
              <div class="k">Compagnie / Opérateur</div>
              <div class="v">{{ selected.operator }}</div>
            </div>
            <div class="kv">
              <div class="k">Type avion</div>
              <div class="v">{{ selected.aircraft }}</div>
            </div>
            <div class="kv">
              <div class="k">Vol</div>
              <div class="v">{{ selected.flightNumber }}</div>
            </div>
            <div class="kv">
              <div class="k">Occupants</div>
              <div class="v">{{ selected.occupants }}</div>
            </div>
          </div>

          <div class="divider"></div>

          <div class="summary">
            <div class="k">Résumé</div>
            <p class="v">{{ selected.summary }}</p>
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

const MOCK = [
  {
    id: "af447",
    title: "Air France 447",
    year: 2009,
    date: "01 Jun 2009",
    location: "Océan Atlantique",
    operator: "Air France",
    aircraft: "Airbus A330-203",
    flightNumber: "AF447",
    occupants: 228,
    fatalities: 228,
    summary:
      "Perte de vitesse indiquée suite au givrage des sondes Pitot, désorientation et décrochage en croisière.",
  },
];

async function pingApi() {
  try {
    const res = await fetch("/api/health");
    apiOk.value = res.ok;
  } catch {
    apiOk.value = false;
  }
}

async function search() {
  const query = q.value.trim();
  if (!query) return;

  loading.value = true;
  selected.value = null;

  try {
    const lower = query.toLowerCase();
    results.value = MOCK.filter(
      (c) =>
        c.title.toLowerCase().includes(lower) ||
        c.location.toLowerCase().includes(lower) ||
        c.operator.toLowerCase().includes(lower) ||
        c.aircraft.toLowerCase().includes(lower) ||
        String(c.year).includes(lower) ||
        c.flightNumber.toLowerCase().includes(lower)
    );

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

onMounted(() => {
  pingApi();
});
</script>
