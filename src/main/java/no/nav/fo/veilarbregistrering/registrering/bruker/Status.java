package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.util.Arrays;

/**
 * Status representerer tilstanden til den aktuelle registreringen, og hvor de er i flyten.
 */
public enum Status {

    MOTTATT("mottatt"),
    ARENA_OK("ok"),
    BRUKER_ER_UKJENT("ukjent"),
    BRUKER_MANGLER_ARBEIDSTILLATELSE("oppholdstillatelse"),
    BRUKER_KAN_IKKE_REAKTIVERES("ikkeReaktivering"),
    BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET("utvandret"),
    TEKNISK_FEIL("teknisk");

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
