package com.naifos.flightcrash.api;

import com.naifos.flightcrash.service.importer.CrashCsvImporter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/import")
public class AdminImportController {

    private final CrashCsvImporter importer;

    public AdminImportController(CrashCsvImporter importer) {
        this.importer = importer;
    }

    @PostMapping("/plane-crashes")
    public String run() throws Exception {
        int n = importer.importFromClasspath();
        return "Imported: " + n;
    }
}
