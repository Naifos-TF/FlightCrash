package com.naifos.flightcrash.repository;

import com.naifos.flightcrash.entity.Crash;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrashRepository extends JpaRepository<Crash, Long> {

    boolean existsByExternalId(String externalId);

    @Query("""
        select c from Crash c
        where
          (
            :q is null
            or :q = ''
            or lower(coalesce(c.operator,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.aircraft,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.crashLocation,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.country,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.region,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.registration,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.flightNo,'')) like lower(concat('%', :q, '%'))
            or lower(coalesce(c.circumstances,'')) like lower(concat('%', :q, '%'))
          )
          and (:year is null or year(c.crashDate) = :year)
          and (:survivors is null or c.survivors = :survivors)
          and (:country is null or :country = '' or c.country = :country)
          and (:region is null or :region = '' or c.region = :region)
          and (:crashCause is null or :crashCause = '' or c.crashCause = :crashCause)
          and (:operator is null or :operator = '' or c.operator = :operator)
          and (:aircraft is null or :aircraft = '' or c.aircraft = :aircraft)
        order by c.crashDate desc nulls last
    """)
    List<Crash> search(
            @Param("q") String q,
            @Param("year") Integer year,
            @Param("survivors") Boolean survivors,
            @Param("country") String country,
            @Param("region") String region,
            @Param("crashCause") String crashCause,
            @Param("operator") String operator,
            @Param("aircraft") String aircraft,
            Pageable pageable
    );

    // Facets

    @Query("""
        select distinct year(c.crashDate)
        from Crash c
        where c.crashDate is not null
        order by year(c.crashDate) desc
    """)
    List<Integer> distinctYears();

    @Query("""
        select distinct c.survivors
        from Crash c
        where c.survivors is not null
        order by c.survivors desc
    """)
    List<Boolean> distinctSurvivors();

    @Query("""
        select distinct c.country
        from Crash c
        where c.country is not null and c.country <> ''
        order by c.country asc
    """)
    List<String> distinctCountries();

    @Query("""
        select distinct c.region
        from Crash c
        where c.region is not null and c.region <> ''
        order by c.region asc
    """)
    List<String> distinctRegions();

    @Query("""
        select distinct c.crashCause
        from Crash c
        where c.crashCause is not null and c.crashCause <> ''
        order by c.crashCause asc
    """)
    List<String> distinctCrashCauses();

    @Query("""
        select distinct c.operator
        from Crash c
        where c.operator is not null and c.operator <> ''
        order by c.operator asc
        """)
    List<String> distinctOperators();

    @Query("""
        select distinct c.aircraft
        from Crash c
        where c.aircraft is not null and c.aircraft <> ''
        order by c.aircraft asc
""")
    List<String> distinctAircrafts();
}
