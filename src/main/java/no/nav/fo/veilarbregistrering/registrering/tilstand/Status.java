package no.nav.fo.veilarbregistrering.registrering.tilstand;

import java.util.Arrays;

/**
 * Status representerer tilstanden til den aktuelle registreringen, og hvor de er i flyten.
 *
 * MOTTATT -> ARENA_OK
 * MOTTATT -> MANGLER_ARBEIDSTILLATELSE -> OPPGAVE_OPPRETTET
 * MOTTATT -> DOD_UTVANDRET_ELLER_FORSVUNNET -> OPPGAVE_OPPRETTET
 * MOTTATT ->
 *
 * Ved å sette status
 */
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
    OPPGAVE_OPPRETTET("oppgaveOpprettet"),
    OPPGAVE_FEILET("oppgaveFeilet"),
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
