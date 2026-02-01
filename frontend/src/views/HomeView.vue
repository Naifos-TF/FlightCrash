<template>
  <div class="appShell">
    <!-- NAVBAR -->
    <header class="nav">
      <div class="navLeft">
        <div class="logo">✈︎</div>
        <div class="brand">
          <div class="brandName">Plane Crash Encyclopedia</div>
          <div class="brandTag">A summary of all plane crashes in history</div>
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
          <h1 class="pageTitle">Search for a crash</h1>
          <p class="pageSub">Airline, location, date, aircraft type, flight number…</p>

          <div class="searchRow">
            <input
              v-model="q"
              class="searchInput"
              type="search"
              placeholder="e.g. Air France, Tenerife, 2009, A320…"
              @keydown.enter="search"
            />
            <button class="primaryBtn" @click="search" :disabled="loading">
              {{ loading ? "Searching…" : "Search" }}
            </button>
          </div>

          <!-- Filters -->
          <div class="filtersGrid">
            <div class="filter">
              <label class="filterLabel">Year</label>
              <select v-model="filters.year" class="filterSelect" @change="search">
                <option value="">All</option>
                <option v-for="y in options.years" :key="y" :value="String(y)">{{ y }}</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Survivors</label>
              <select v-model="filters.survivors" class="filterSelect" @change="search">
                <option value="">All</option>
                <option value="true">Yes</option>
                <option value="false">No</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Country</label>
              <select v-model="filters.country" class="filterSelect" @change="search">
                <option value="">All</option>
                <option v-for="c in options.countries" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Region</label>
              <select v-model="filters.region" class="filterSelect" @change="search">
                <option value="">All</option>
                <option v-for="r in options.regions" :key="r" :value="r">{{ r }}</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Operator</label>
              <select v-model="filters.operator" class="filterSelect" @change="search">
                <option value="">All</option>
                <option v-for="op in options.operators" :key="op" :value="op">{{ op }}</option>
              </select>
            </div>

            <div class="filter">
              <label class="filterLabel">Aircraft</label>
              <select v-model="filters.aircraft" class="filterSelect" @change="search">
                <option value="">All</option>
                <option v-for="ac in options.aircrafts" :key="ac" :value="ac">{{ ac }}</option>
              </select>
            </div>

            <div class="filter filterSpan2">
              <label class="filterLabel">Crash cause</label>
              <select v-model="filters.crashCause" class="filterSelect" @change="search">
                <option value="">All</option>
                <option v-for="cc in options.crashCauses" :key="cc" :value="cc">{{ cc }}</option>
              </select>
            </div>
          </div>

          <div class="searchActions">
            <button class="ghostBtn" @click="reset" :disabled="loading">
              Reset
            </button>
          </div>
        </div>

        <div class="resultsBlock">
          <div class="resultsHeader">
            <h2 class="sectionTitle">Results</h2>
            <span class="count">{{ results.length }}</span>
          </div>

          <div v-if="!results.length && !loading" class="empty">
            No results. Run a search.
          </div>

          <div v-if="loading" class="empty">Loading…</div>

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
        <div v-if="!selected" class="empty detailsEmpty">
          Select a result to view details.
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
              <div class="k">Operator</div>
              <div class="v">{{ selected.operator ?? "—" }}</div>
            </div>
            <div class="kv">
              <div class="k">Aircraft type</div>
              <div class="v">{{ selected.aircraft ?? "—" }}</div>
            </div>
            <div class="kv">
              <div class="k">Flight</div>
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
              <div class="v">{{ selected.survivors === true ? "Yes" : selected.survivors === false ? "No" : "—" }}</div>
            </div>
            <div class="kv filterSpan2">
              <div class="k">Crash cause</div>
              <div class="v">{{ selected.crashCause ?? "—" }}</div>
            </div>
          </div>

          <div class="divider"></div>

          <div class="summary">
            <div class="k">Circumstances</div>
            <p class="v">{{ selected.circumstances ?? "—" }}</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>
