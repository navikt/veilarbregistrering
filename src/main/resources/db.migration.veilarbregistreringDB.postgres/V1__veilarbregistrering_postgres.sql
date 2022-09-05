CREATE TABLE begrunnelse_kodeverk (
                                      begrunnelse_id bigint,
                                      begrunnelse_kode varchar(30)
);

CREATE TABLE bruker_profilering (
                                    bruker_registrering_id bigint,
                                    profilering_type varchar(30),
                                    verdi varchar(200)
);

CREATE TABLE bruker_profilering_kodeverk (
                                             profilering_kode_id bigint,
                                             profilering_kode varchar(30)
);

CREATE TABLE bruker_reaktivering (
                                     bruker_reaktivering_id bigint,
                                     aktor_id varchar(20),
                                     reaktivering_dato timestamp(6)
);

CREATE TABLE bruker_registrering (
                                     bruker_registrering_id bigint,
                                     aktor_id varchar(20),
                                     opprettet_dato timestamp(6),
                                     nus_kode varchar(6),
                                     yrkespraksis varchar(7),
                                     yrkesbeskrivelse varchar(200),
                                     konsept_id bigint,
                                     andre_utfordringer varchar(30),
                                     begrunnelse_for_registrering varchar(30),
                                     utdanning_bestatt varchar(30),
                                     utdanning_godkjent_norge varchar(30),
                                     jobbhistorikk varchar(30),
                                     har_helseutfordringer varchar(30),
                                     tekster_for_besvarelse varchar(4000),
                                     foedselsnummer varchar(11)
);

CREATE TABLE formidlingsgruppe (
                                   id bigint,
                                   foedselsnummer varchar(11),
                                   formidlingsgruppe varchar(5),
                                   formidlingsgruppe_endret timestamp(6),
                                   formidlingsgruppe_lest timestamp(6),
                                   person_id varchar(8),
                                   forr_formidlingsgruppe_endret timestamp(6),
                                   forr_formidlingsgruppe varchar(5),
                                   operasjon varchar(10),
                                   person_id_status varchar(20)
);

CREATE TABLE manuell_registrering (
                                      manuell_registrering_id bigint,
                                      veileder_ident varchar(10),
                                      veileder_enhet_id varchar(4),
                                      registrering_id bigint,
                                      bruker_registrering_type varchar(20)
);

CREATE TABLE oppgave (
                         id bigint,
                         aktor_id varchar(20),
                         oppgavetype varchar(36),
                         ekstern_oppgave_id bigint,
                         opprettet timestamp(6)
);

CREATE TABLE registrering_tilstand (
                                       id bigint,
                                       bruker_registrering_id bigint,
                                       opprettet timestamp(6),
                                       sist_endret timestamp(6),
                                       status varchar(36)
);

CREATE TABLE situasjon_kodeverk (
                                    situasjon_id bigint,
                                    situasjon_kode varchar(30)
);

CREATE TABLE sykmeldt_registrering (
                                       sykmeldt_registrering_id bigint,
                                       aktor_id varchar(20),
                                       opprettet_dato timestamp(6),
                                       tekster_for_besvarelse varchar(4000),
                                       fremtidig_situasjon varchar(30),
                                       tilbake_etter_52_uker varchar(30),
                                       nus_kode varchar(6),
                                       utdanning_bestatt varchar(30),
                                       utdanning_godkjent_norge varchar(30),
                                       andre_utfordringer varchar(30)
);

CREATE TABLE tilbake_besvarelse_kodeverk (
                                             tilbake_besvarelse_id bigint,
                                             tilbake_besvarelse_kode varchar(30)
);


ALTER TABLE registrering_tilstand ALTER COLUMN id SET NOT NULL;

ALTER TABLE registrering_tilstand ALTER COLUMN bruker_registrering_id SET NOT NULL;

ALTER TABLE registrering_tilstand ALTER COLUMN opprettet SET NOT NULL;

ALTER TABLE registrering_tilstand ALTER COLUMN status SET NOT NULL;

ALTER TABLE oppgave ALTER COLUMN id SET NOT NULL;

ALTER TABLE oppgave ALTER COLUMN aktor_id SET NOT NULL;

ALTER TABLE oppgave ALTER COLUMN oppgavetype SET NOT NULL;

ALTER TABLE oppgave ALTER COLUMN ekstern_oppgave_id SET NOT NULL;

ALTER TABLE oppgave ALTER COLUMN opprettet SET NOT NULL;

ALTER TABLE sykmeldt_registrering ALTER COLUMN sykmeldt_registrering_id SET NOT NULL;

ALTER TABLE sykmeldt_registrering ALTER COLUMN aktor_id SET NOT NULL;

ALTER TABLE sykmeldt_registrering ALTER COLUMN opprettet_dato SET NOT NULL;

ALTER TABLE sykmeldt_registrering ALTER COLUMN tekster_for_besvarelse SET NOT NULL;

ALTER TABLE sykmeldt_registrering ALTER COLUMN fremtidig_situasjon SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN id SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN foedselsnummer SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN formidlingsgruppe SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN formidlingsgruppe_endret SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN formidlingsgruppe_lest SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN person_id SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN operasjon SET NOT NULL;

ALTER TABLE formidlingsgruppe ALTER COLUMN person_id_status SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN aktor_id SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN opprettet_dato SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN nus_kode SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN yrkespraksis SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN yrkesbeskrivelse SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN konsept_id SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN andre_utfordringer SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN begrunnelse_for_registrering SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN utdanning_bestatt SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN utdanning_godkjent_norge SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN jobbhistorikk SET NOT NULL;

ALTER TABLE bruker_registrering ALTER COLUMN tekster_for_besvarelse SET NOT NULL;

ALTER TABLE manuell_registrering ALTER COLUMN veileder_ident SET NOT NULL;

ALTER TABLE manuell_registrering ALTER COLUMN veileder_enhet_id SET NOT NULL;

ALTER TABLE bruker_profilering ALTER COLUMN profilering_type SET NOT NULL;

ALTER TABLE bruker_profilering ALTER COLUMN verdi SET NOT NULL;

ALTER TABLE sykmeldt_registrering ALTER COLUMN sykmeldt_registrering_id SET NOT NULL;
ALTER TABLE bruker_profilering_kodeverk ALTER COLUMN profilering_kode_id SET NOT NULL;
ALTER TABLE bruker_reaktivering ALTER COLUMN bruker_reaktivering_id SET NOT NULL;
ALTER TABLE begrunnelse_kodeverk ALTER COLUMN begrunnelse_id SET NOT NULL;
ALTER TABLE bruker_registrering ALTER COLUMN bruker_registrering_id SET NOT NULL;
ALTER TABLE situasjon_kodeverk ALTER COLUMN situasjon_id SET NOT NULL;
ALTER TABLE manuell_registrering ALTER COLUMN manuell_registrering_id SET NOT NULL;
ALTER TABLE bruker_profilering ALTER COLUMN bruker_registrering_id SET NOT NULL;
ALTER TABLE tilbake_besvarelse_kodeverk ALTER COLUMN tilbake_besvarelse_id SET NOT NULL;

CREATE SEQUENCE bruker_reaktivering_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 1021
    CACHE 20 NO CYCLE;

CREATE SEQUENCE bruker_registrering_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 4487
    CACHE 20 NO CYCLE;

CREATE SEQUENCE formidlingsgruppe_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 13433835
    CACHE 20 NO CYCLE;

CREATE SEQUENCE manuell_registrering_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 2347
    CACHE 20 NO CYCLE;

CREATE SEQUENCE oppgave_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 1060
    CACHE 20 NO CYCLE;

CREATE SEQUENCE registrering_tilstand_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 3867
    CACHE 20 NO CYCLE;

CREATE SEQUENCE sykmeldt_registrering_seq
    MINVALUE 1
    /* Warning: MAXVALUE 9999999999999999999999999999 */
    INCREMENT BY 1 START WITH 301
    CACHE 20 NO CYCLE;

CREATE INDEX formidlingsgruppe_fnr_index ON formidlingsgruppe (foedselsnummer);

CREATE INDEX bruker_reg_aktor_id_index ON bruker_registrering (aktor_id);

CREATE INDEX formgrp_funksjonell_id_index ON formidlingsgruppe (person_id, formidlingsgruppe, formidlingsgruppe_endret);

CREATE INDEX sykmeldt_reg_aktor_id_index ON sykmeldt_registrering (aktor_id);


CREATE INDEX reg_tilstand_status_index ON registrering_tilstand (status);

ALTER TABLE registrering_tilstand
    ADD CONSTRAINT pk_registrering_tilstand PRIMARY KEY (id);

ALTER TABLE registrering_tilstand
    ADD CONSTRAINT bruker_registrering_id_unique UNIQUE (bruker_registrering_id);

ALTER TABLE oppgave
    ADD CONSTRAINT oppgave_pk PRIMARY KEY (id);

ALTER TABLE sykmeldt_registrering
    ADD CONSTRAINT sykmeldt_registrering_pk PRIMARY KEY (sykmeldt_registrering_id);

ALTER TABLE formidlingsgruppe
    ADD CONSTRAINT formidlingsgruppe_pk PRIMARY KEY (id);

ALTER TABLE bruker_profilering_kodeverk
    ADD CONSTRAINT unique_constraint_profilering UNIQUE (profilering_kode);

ALTER TABLE bruker_profilering_kodeverk
    ADD CONSTRAINT bruker_profilering_kodeverk_pk PRIMARY KEY (profilering_kode_id);

ALTER TABLE bruker_reaktivering
    ADD CONSTRAINT bruker_reaktivering_pk PRIMARY KEY (bruker_reaktivering_id);

ALTER TABLE begrunnelse_kodeverk
    ADD CONSTRAINT begrunnelse_kodeverk_pk PRIMARY KEY (begrunnelse_id);

ALTER TABLE begrunnelse_kodeverk
    ADD CONSTRAINT unique_constraint_begrunnelse UNIQUE (begrunnelse_kode);

ALTER TABLE bruker_registrering
    ADD CONSTRAINT bruker_registrering_pk PRIMARY KEY (bruker_registrering_id);

ALTER TABLE situasjon_kodeverk
    ADD CONSTRAINT situasjon_kodeverk_pk PRIMARY KEY (situasjon_id);

ALTER TABLE situasjon_kodeverk
    ADD CONSTRAINT unique_constraint_situasjon UNIQUE (situasjon_kode);

ALTER TABLE manuell_registrering
    ADD CONSTRAINT manuell_registrering_pk PRIMARY KEY (manuell_registrering_id);

ALTER TABLE bruker_profilering
    ADD CONSTRAINT bruker_profilering_pk PRIMARY KEY (bruker_registrering_id, profilering_type);

ALTER TABLE tilbake_besvarelse_kodeverk
    ADD CONSTRAINT unique_constraint_tilbake UNIQUE (tilbake_besvarelse_kode);

ALTER TABLE tilbake_besvarelse_kodeverk
    ADD CONSTRAINT tilbake_kodeverk_pk PRIMARY KEY (tilbake_besvarelse_id);

ALTER TABLE bruker_profilering
    ADD CONSTRAINT profilering_type_fk FOREIGN KEY (profilering_type) REFERENCES bruker_profilering_kodeverk (profilering_kode);

ALTER TABLE bruker_registrering
    ADD CONSTRAINT begrunnelse_registrering_fk FOREIGN KEY (begrunnelse_for_registrering) REFERENCES begrunnelse_kodeverk (begrunnelse_kode);

ALTER TABLE registrering_tilstand
    ADD CONSTRAINT bruker_registrering_id_fk FOREIGN KEY (bruker_registrering_id) REFERENCES bruker_registrering (bruker_registrering_id);

ALTER TABLE sykmeldt_registrering
    ADD CONSTRAINT tilbake_kodeverk_fk FOREIGN KEY (tilbake_etter_52_uker) REFERENCES tilbake_besvarelse_kodeverk (tilbake_besvarelse_kode);

ALTER TABLE sykmeldt_registrering
    ADD CONSTRAINT situasjon_kodeverk_fk FOREIGN KEY (fremtidig_situasjon) REFERENCES situasjon_kodeverk (situasjon_kode);

INSERT INTO BRUKER_PROFILERING_KODEVERK VALUES
                                            (1, 'ALDER'),
                                            (2, 'ARB_6_AV_SISTE_12_MND'),
                                            (3, 'RESULTAT_PROFILERING');

INSERT INTO BEGRUNNELSE_KODEVERK VALUES
                                     (1, 'MISTET_JOBBEN'),
                                     (2, 'HAR_SAGT_OPP'),
                                     (3, 'ER_PERMITTERT'),
                                     (4, 'JOBB_OVER_2_AAR'),
                                     (5, 'ALDRI_HATT_JOBB'),
                                     (6, 'VIL_BYTTE_JOBB'),
                                     (7, 'USIKKER_JOBBSITUASJON'),
                                     (8, 'VIL_FORTSETTE_I_JOBB'),
                                     (9, 'AKKURAT_FULLFORT_UTDANNING'),
                                     (10, 'DELTIDSJOBB_VIL_MER');

INSERT INTO SITUASJON_KODEVERK VALUES
                                   (1, 'SAMME_ARBEIDSGIVER'),
                                   (2, 'SAMME_ARBEIDSGIVER_NY_STILLING'),
                                   (3, 'NY_ARBEIDSGIVER'),
                                   (4, 'USIKKER'),
                                   (5, 'INGEN_PASSER');

INSERT INTO TILBAKE_BESVARELSE_KODEVERK VALUES
                                            (1, 'JA_FULL_STILLING'),
                                            (2, 'JA_REDUSERT_STILLING'),
                                            (3, 'USIKKER'),
                                            (4, 'NEI');