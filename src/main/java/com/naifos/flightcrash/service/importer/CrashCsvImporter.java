package com.naifos.flightcrash.service.importer;

import com.naifos.flightcrash.entity.Crash;
import com.naifos.flightcrash.repository.CrashRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrashCsvImporter {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)H\\s+(\\d+)M\\s+(\\d+)S");
    private static final int BATCH_SIZE = 500;

    private final CrashRepository repo;

    public CrashCsvImporter(CrashRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public int importFromClasspath() throws Exception {
        var resource = new ClassPathResource("data/Plane Crashes.csv");

        try (var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            var parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);

            int inserted = 0;
            List<Crash> buffer = new ArrayList<>(BATCH_SIZE);

            for (CSVRecord r : parser) {
                // Option recommandée: rendre l'external_id unique en ajoutant le numéro de ligne du CSV
                long recordNumber = r.getRecordNumber();

                String externalId = sha256(
                        nvl(get(r, "Date")) + "|" +
                                nvl(get(r, "Time")) + "|" +
                                nvl(get(r, "Aircraft")) + "|" +
                                nvl(get(r, "Operator")) + "|" +
                                nvl(get(r, "Registration")) + "|" +
                                nvl(get(r, "Flight no.")) + "|" +
                                nvl(get(r, "Crash location")) + "|" +
                                recordNumber
                );

                // Comme tu importes une seule fois, tu peux garder ce check (optionnel)
                if (repo.existsByExternalId(externalId)) {
                    continue;
                }

                Crash c = new Crash();
                c.setExternalId(externalId);

                c.setCrashDate(parseDate(get(r, "Date")));
                c.setCrashTime(parseTime(get(r, "Time")));

                c.setAircraft(get(r, "Aircraft"));
                c.setOperator(get(r, "Operator"));
                c.setRegistration(get(r, "Registration"));
                c.setFlightPhase(get(r, "Flight phase"));
                c.setFlightType(get(r, "Flight type"));
                c.setSurvivors(parseYesNo(get(r, "Survivors")));

                c.setCrashSite(get(r, "Crash site"));
                c.setSchedule(get(r, "Schedule"));
                c.setMsn(get(r, "MSN"));
                c.setYom(parseInt(get(r, "YOM")));
                c.setFlightNo(get(r, "Flight no."));

                c.setCrashLocation(get(r, "Crash location"));
                c.setCountry(get(r, "Country"));
                c.setRegion(get(r, "Region"));

                c.setCrewOnBoard(parseInt(get(r, "Crew on board")));
                c.setCrewFatalities(parseInt(get(r, "Crew fatalities")));
                c.setPaxOnBoard(parseInt(get(r, "Pax on board")));
                c.setPaxFatalities(parseInt(get(r, "PAX fatalities")));
                c.setOtherFatalities(parseInt(get(r, "Other fatalities")));
                c.setTotalFatalities(parseInt(get(r, "Total fatalities")));

                c.setCircumstances(get(r, "Circumstances"));
                c.setCrashCause(get(r, "Crash cause"));

                buffer.add(c);

                if (buffer.size() >= BATCH_SIZE) {
                    repo.saveAll(buffer);
                    inserted += buffer.size();
                    buffer.clear();
                }
            }

            if (!buffer.isEmpty()) {
                repo.saveAll(buffer);
                inserted += buffer.size();
            }

            return inserted;
        }
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private static String get(CSVRecord r, String key) {
        try {
            String v = r.get(key);
            return (v == null || v.isBlank()) ? null : v.trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDate parseDate(String s) {
        if (s == null) return null;
        return LocalDate.parse(s); // YYYY-MM-DD
    }

    private static LocalTime parseTime(String s) {
        if (s == null) return null;
        Matcher m = TIME_PATTERN.matcher(s);
        if (!m.matches()) return null;
        int h = Integer.parseInt(m.group(1));
        int min = Integer.parseInt(m.group(2));
        int sec = Integer.parseInt(m.group(3));
        if (h >= 24) return null;
        return LocalTime.of(h, min, sec);
    }

    private static Boolean parseYesNo(String s) {
        if (s == null) return null;
        return switch (s.trim().toLowerCase()) {
            case "yes" -> true;
            case "no" -> false;
            default -> null;
        };
    }

    private static Integer parseInt(String s) {
        if (s == null) return null;
        try {
            double d = Double.parseDouble(s);
            return (int) d;
        } catch (Exception e) {
            return null;
        }
    }

    private static String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
