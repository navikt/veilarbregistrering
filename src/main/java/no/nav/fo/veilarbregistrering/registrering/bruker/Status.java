package no.nav.fo.veilarbregistrering.registrering.bruker;

/**
 * Status representerer tilstanden til den aktuelle registreringen, og hvor de er i flyten.
 */
public enum Status {

    MOTTATT,
    ARENA_OK,
    BRUKER_ER_UKJENT,
    BRUKER_MANGLER_ARBEIDSTILLATELSE,
    BRUKER_KAN_IKKE_REAKTIVERES,
    BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET,
    DØD_UTVANDRET,
    TEKNISK_FEIL;

}
