create table if not exists crash (
                                     id bigserial primary key,

    -- idempotence / anti-doublons
                                     external_id varchar(64) not null unique,

    crash_date date,
    crash_time time,

    aircraft text,
    operator text,
    registration text,
    flight_phase text,
    flight_type text,
    survivors boolean,

    crash_site text,
    schedule text,
    msn text,
    yom int,
    flight_no text,

    crash_location text,
    country text,
    region text,

    crew_on_board int,
    crew_fatalities int,
    pax_on_board int,
    pax_fatalities int,
    other_fatalities int,
    total_fatalities int,

    circumstances text,
    crash_cause text,

    created_at timestamptz not null default now()
    );

create index if not exists idx_crash_date on crash(crash_date);
create index if not exists idx_crash_country on crash(country);
create index if not exists idx_crash_operator on crash(operator);
create index if not exists idx_crash_aircraft on crash(aircraft);
