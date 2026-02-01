package com.naifos.flightcrash.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "crash")
public class Crash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="external_id", nullable=false, unique=true, length=64)
    private String externalId;

    @Column(name="crash_date")
    private LocalDate crashDate;

    @Column(name="crash_time")
    private LocalTime crashTime;

    private String aircraft;
    private String operator;
    private String registration;

    @Column(name="flight_phase")
    private String flightPhase;

    @Column(name="flight_type")
    private String flightType;

    private Boolean survivors;

    @Column(name="crash_site")
    private String crashSite;

    private String schedule;
    private String msn;
    private Integer yom;

    @Column(name="flight_no")
    private String flightNo;

    @Column(name="crash_location")
    private String crashLocation;

    private String country;
    private String region;

    @Column(name="crew_on_board")
    private Integer crewOnBoard;

    @Column(name="crew_fatalities")
    private Integer crewFatalities;

    @Column(name="pax_on_board")
    private Integer paxOnBoard;

    @Column(name="pax_fatalities")
    private Integer paxFatalities;

    @Column(name="other_fatalities")
    private Integer otherFatalities;

    @Column(name="total_fatalities")
    private Integer totalFatalities;

    @Column(columnDefinition="text")
    private String circumstances;

    @Column(name="crash_cause")
    private String crashCause;

    /* --- computed --- */
    @Transient
    public Integer getOccupants() {
        Integer crew = crewOnBoard;
        Integer pax = paxOnBoard;
        if (crew == null && pax == null) return null;
        return (crew == null ? 0 : crew) + (pax == null ? 0 : pax);
    }

    /* --- getters/setters --- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public LocalDate getCrashDate() { return crashDate; }
    public void setCrashDate(LocalDate crashDate) { this.crashDate = crashDate; }

    public LocalTime getCrashTime() { return crashTime; }
    public void setCrashTime(LocalTime crashTime) { this.crashTime = crashTime; }

    public String getAircraft() { return aircraft; }
    public void setAircraft(String aircraft) { this.aircraft = aircraft; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }

    public String getFlightPhase() { return flightPhase; }
    public void setFlightPhase(String flightPhase) { this.flightPhase = flightPhase; }

    public String getFlightType() { return flightType; }
    public void setFlightType(String flightType) { this.flightType = flightType; }

    public Boolean getSurvivors() { return survivors; }
    public void setSurvivors(Boolean survivors) { this.survivors = survivors; }

    public String getCrashSite() { return crashSite; }
    public void setCrashSite(String crashSite) { this.crashSite = crashSite; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getMsn() { return msn; }
    public void setMsn(String msn) { this.msn = msn; }

    public Integer getYom() { return yom; }
    public void setYom(Integer yom) { this.yom = yom; }

    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { this.flightNo = flightNo; }

    public String getCrashLocation() { return crashLocation; }
    public void setCrashLocation(String crashLocation) { this.crashLocation = crashLocation; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Integer getCrewOnBoard() { return crewOnBoard; }
    public void setCrewOnBoard(Integer crewOnBoard) { this.crewOnBoard = crewOnBoard; }

    public Integer getCrewFatalities() { return crewFatalities; }
    public void setCrewFatalities(Integer crewFatalities) { this.crewFatalities = crewFatalities; }

    public Integer getPaxOnBoard() { return paxOnBoard; }
    public void setPaxOnBoard(Integer paxOnBoard) { this.paxOnBoard = paxOnBoard; }

    public Integer getPaxFatalities() { return paxFatalities; }
    public void setPaxFatalities(Integer paxFatalities) { this.paxFatalities = paxFatalities; }

    public Integer getOtherFatalities() { return otherFatalities; }
    public void setOtherFatalities(Integer otherFatalities) { this.otherFatalities = otherFatalities; }

    public Integer getTotalFatalities() { return totalFatalities; }
    public void setTotalFatalities(Integer totalFatalities) { this.totalFatalities = totalFatalities; }

    public String getCircumstances() { return circumstances; }
    public void setCircumstances(String circumstances) { this.circumstances = circumstances; }

    public String getCrashCause() { return crashCause; }
    public void setCrashCause(String crashCause) { this.crashCause = crashCause; }
}
