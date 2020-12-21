package no.nav.fo.veilarbregistrering.registrering.tilstand;

public enum Status2 {
    MOTTATT("mottatt"),
    OVERFORT_ARENA("overfort_arena"),
    PUBLISERT_KAFKA("publisertKafka"),
    UKJENT_BRUKER("ukjentBruker"),
    MANGLER_ARBEIDSTILLATELSE("oppholdstillatelse"),
    KAN_IKKE_REAKTIVERES("ikkeReaktivering"),
    DOD_UTVANDRET_ELLER_FORSVUNNET("utvandret"),
    UKJENT_TEKNISK_FEIL("ukjentTeknisk"),
    TEKNISK_FEIL("teknisk"),
    OPPRINNELIG_OPPRETTET_UTEN_TILSTAND("opprinneligOpprettetUtenTilstand");

    private final String status;

    Status2(String status) {
        this.status = status;
    }
}