CREATE TABLE meldekort (
    id bigint primary key,
    foedselsnummer varchar not null,
    er_arbeidssoker_neste_periode boolean not null,
    periode_fra date not null,
    periode_til date not null,
    meldekorttype varchar not null,
    meldekort_event_id int not null,
    event_opprettet timestamp(6) not null
);

CREATE INDEX meldekort_foedselsnummer_index ON meldekort(foedselsnummer);

CREATE SEQUENCE meldekort_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 1
    CACHE 20 NO CYCLE;