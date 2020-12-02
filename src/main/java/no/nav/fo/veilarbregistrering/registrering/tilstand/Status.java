package no.nav.fo.veilarbregistrering.registrering.tilstand;

import java.util.Arrays;

public enum Status {

    MOTTATT("mottatt"),
    ARENA_OK("ok"),
    OVERFORT_ARENA("overfort_arena"),
    PUBLISERT_KAFKA("publisertKafka"),
    UKJENT_BRUKER("ukjentBruker"),
    MANGLER_ARBEIDSTILLATELSE("oppholdstillatelse"),
    KAN_IKKE_REAKTIVERES("ikkeReaktivering"),
    DOD_UTVANDRET_ELLER_FORSVUNNET("utvandret"),
    UKJENT_TEKNISK_FEIL("ukjentTeknisk"),
    TEKNISK_FEIL("teknisk"),
    OPPRINNELIG_OPPRETTET_UTEN_TILSTAND("opprinneligOpprettetUtenTilstand"),
    ;

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public static Status parse(String status) {
        return Arrays.stream(Status.values())
                .filter(s -> s.status.equals(status))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
