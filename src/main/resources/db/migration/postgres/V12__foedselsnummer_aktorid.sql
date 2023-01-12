create table aktor_id_cache (
    foedselsnummer VARCHAR not null,
    aktor_id VARCHAR not null,
    opprettet_dato TIMESTAMP(1) not null
);

create unique index foedselsnummer_idx on aktor_id_cache(foedselsnummer);