package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;

import java.util.Optional;

public enum AdressebeskyttelseGradering {

    /**
     * Tilsvarer paragraf 19 i Bisys (henvisning til Forvaltningslovens §19)
     * Koden finnes kun i PDL, og ikke i TPS.
     * Routing skjer (foreløpig) basert på TPS, så vi må eksplisitt overstyre enhet.
     * Eksplisitt routing-enhet kan fjernes når Oppgave-APIet går over til PDL.
     */
    STRENGT_FORTROLIG_UTLAND(Enhetnr.Companion.enhetForAdressebeskyttelse()),

    /** Tidligere spesregkode kode 6 fra TPS */
    STRENGT_FORTROLIG,

    /** Tidligere spesregkode kode 7 fra TPS */
    FORTROLIG,

    /** Kode vi kan få fra Folkeregisteret, men brukscaset er ukjent. */
    UGRADERT,

    UKJENT;

    private final Enhetnr eksplisittRoutingEnhet;

    public boolean erGradert() {
        return this != UGRADERT && this != UKJENT;
    }

    AdressebeskyttelseGradering() {
        this(null);
    }

    AdressebeskyttelseGradering(Enhetnr eksplisittRoutingEnhet) {
        this.eksplisittRoutingEnhet = eksplisittRoutingEnhet;
    }

    public Optional<Enhetnr> getEksplisittRoutingEnhet() {
        return Optional.ofNullable(eksplisittRoutingEnhet);
    }
}
