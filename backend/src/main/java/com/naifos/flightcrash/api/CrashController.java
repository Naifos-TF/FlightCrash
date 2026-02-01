package com.naifos.flightcrash.api;

import com.naifos.flightcrash.entity.Crash;
import com.naifos.flightcrash.repository.CrashRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crashes")
public class CrashController {

    private final CrashRepository repo;

    public CrashController(CrashRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/search")
    public List<Crash> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean survivors,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String crashCause,
            @RequestParam(defaultValue = "50") int limit
    ) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repo.search(q, year, survivors, country, region, crashCause, PageRequest.of(0, safeLimit));
    }

    @GetMapping("/facets")
    public CrashFacets facets() {
        // Survivors: on renvoie [true,false] si dispo, sinon []
        return new CrashFacets(
                repo.distinctYears(),
                repo.distinctSurvivors(),
                repo.distinctCountries(),
                repo.distinctRegions(),
                repo.distinctCrashCauses()
        );
    }

    public record CrashFacets(
            List<Integer> years,
            List<Boolean> survivors,
            List<String> countries,
            List<String> regions,
            List<String> crashCauses
    ) {}
}
