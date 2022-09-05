CREATE OR REPLACE VIEW dvh_begrunnelse_kodeverk (begrunnelse_id, begrunnelse_kode)
AS (
   SELECT
       BEGRUNNELSE_ID,
       BEGRUNNELSE_KODE
   FROM
       BEGRUNNELSE_KODEVERK);

CREATE OR REPLACE VIEW dvh_bruker_profilering (bruker_registrering_id, profilering_type, verdi)
AS (
   SELECT
       BRUKER_PROFILERING.BRUKER_REGISTRERING_ID,
       PROFILERING_TYPE,
       VERDI
   FROM
       BRUKER_PROFILERING
           INNER JOIN REGISTRERING_TILSTAND ON REGISTRERING_TILSTAND.BRUKER_REGISTRERING_ID = BRUKER_PROFILERING.BRUKER_REGISTRERING_ID
   WHERE
           REGISTRERING_TILSTAND.STATUS IN ('OVERFORT_ARENA', 'PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND'));

CREATE OR REPLACE VIEW dvh_bruker_reaktivering (bruker_reaktivering_id, aktor_id, reaktivering_dato)
AS (
   SELECT
       BRUKER_REAKTIVERING_ID,
       AKTOR_ID,
       REAKTIVERING_DATO
   FROM
       BRUKER_REAKTIVERING);

CREATE OR REPLACE VIEW dvh_bruker_registrering (bruker_registrering_id, aktor_id, opprettet_dato, yrkespraksis, nus_kode, yrkesbeskrivelse, konsept_id, utdanning_bestatt, utdanning_godkjent_norge, helse_utfordringer, andre_utfordringer, begrunnelse_for_registrering)
AS (
   SELECT
       BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID,
       AKTOR_ID,
       BRUKER_REGISTRERING.OPPRETTET_DATO,
       YRKESPRAKSIS,
       NUS_KODE,
       YRKESBESKRIVELSE,
       KONSEPT_ID,
       CASE UTDANNING_BESTATT
           WHEN 'JA' THEN
               1
           WHEN 'NEI' THEN
               0
           ELSE
               - 1
           END AS UTDANNING_BESTATT,
       CASE UTDANNING_GODKJENT_NORGE
           WHEN 'JA' THEN
               1
           WHEN 'NEI' THEN
               0
           WHEN 'VET_IKKE' THEN
               2
           ELSE
               - 1
           END AS UTDANNING_GODKJENT_NORGE,
       CASE HAR_HELSEUTFORDRINGER
           WHEN 'JA' THEN
               1
           WHEN 'NEI' THEN
               0
           ELSE
               - 1
           END AS HELSE_UTFORDRINGER,
       CASE ANDRE_UTFORDRINGER
           WHEN 'JA' THEN
               1
           WHEN 'NEI' THEN
               0
           ELSE
               - 1
           END AS ANDRE_UTFORDRINGER,
       BEGRUNNELSE_FOR_REGISTRERING
   FROM
       BRUKER_REGISTRERING
           INNER JOIN REGISTRERING_TILSTAND ON REGISTRERING_TILSTAND.BRUKER_REGISTRERING_ID = BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID
   WHERE
           REGISTRERING_TILSTAND.STATUS IN ('OVERFORT_ARENA', 'PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND'));

CREATE OR REPLACE VIEW dvh_bruker_registrering_tekst (bruker_registrering_id, tekster_for_besvarelse)
AS (
   SELECT
       BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID,
       TEKSTER_FOR_BESVARELSE
   FROM
       BRUKER_REGISTRERING
           INNER JOIN REGISTRERING_TILSTAND ON REGISTRERING_TILSTAND.BRUKER_REGISTRERING_ID = BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID
   WHERE
           REGISTRERING_TILSTAND.STATUS IN ('OVERFORT_ARENA', 'PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND'));

CREATE OR REPLACE VIEW dvh_profilering_kodeverk (profilering_kode_id, profilering_kode)
AS (
   SELECT
       PROFILERING_KODE_ID,
       PROFILERING_KODE
   FROM
       BRUKER_PROFILERING_KODEVERK);

CREATE OR REPLACE VIEW dvh_situasjon_kodeverk (situasjon_id, situasjon_kode)
AS (
   SELECT
       SITUASJON_ID,
       SITUASJON_KODE
   FROM
       SITUASJON_KODEVERK);

CREATE OR REPLACE VIEW dvh_sykmeldt_registrering (sykmeldt_registrering_id, aktor_id, opprettet_dato, fremtidig_situasjon, tilbake_etter_52_uker, nus_kode, utdanning_bestatt, utdanning_godkjent_norge, andre_utfordringer)
AS (
   SELECT
       SYKMELDT_REGISTRERING_ID,
       AKTOR_ID,
       OPPRETTET_DATO,
       FREMTIDIG_SITUASJON,
       TILBAKE_ETTER_52_UKER,
       NUS_KODE,
       CASE UTDANNING_BESTATT
           WHEN 'JA' THEN
               1
           WHEN 'NEI' THEN
               0
           ELSE
               - 1
           END AS UTDANNING_BESTATT,
       CASE UTDANNING_GODKJENT_NORGE
           WHEN 'JA' THEN
               1
           WHEN 'NEI' THEN
               0
           ELSE
               - 1
           END AS UTDANNING_GODKJENT_NORGE,
       ANDRE_UTFORDRINGER
   FROM
       SYKMELDT_REGISTRERING);

CREATE OR REPLACE VIEW dvh_sykmeldt_reg_tekst (sykmeldt_registrering_id, tekster_for_besvarelse)
AS (
   SELECT
       SYKMELDT_REGISTRERING_ID,
       TEKSTER_FOR_BESVARELSE
   FROM
       SYKMELDT_REGISTRERING);

CREATE OR REPLACE VIEW dvh_tilbake_kodeverk (tilbake_besvarelse_id, tilbake_besvarelse_kode)
AS (
   SELECT
       TILBAKE_BESVARELSE_ID,
       TILBAKE_BESVARELSE_KODE
   FROM
       TILBAKE_BESVARELSE_KODEVERK);