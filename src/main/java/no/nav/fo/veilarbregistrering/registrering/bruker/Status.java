package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.util.Arrays;

/**
 * Status representerer tilstanden til den aktuelle registreringen, og hvor de er i flyten.
 */
public enum Status {

    MOTTATT("mottatt"),
    ARENA_OK("ok"),
    UKJENT_BRUKER("ukjentBruker"),
    MANGLER_ARBEIDSTILLATELSE("oppholdstillatelse"),
    KAN_IKKE_REAKTIVERES("ikkeReaktivering"),
    DOD_UTVANDRET_ELLER_FORSVUNNET("utvandret"),
    UKJENT_TEKNISK_FEIL("ukjentTeknisk"),
    TEKNISK_FEIL("teknisk"),
    OPPGAVE_OPPRETTET("oppgaveOpprettet"),
    OPPGAVE_FEILET("oppgaveFeilet");

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
