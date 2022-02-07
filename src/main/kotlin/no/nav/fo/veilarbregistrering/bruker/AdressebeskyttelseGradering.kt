package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.enhetForAdressebeskyttelse

enum class AdressebeskyttelseGradering constructor(val eksplisittRoutingEnhet: Enhetnr? = null) {
    /**
     * Tilsvarer paragraf 19 i Bisys (henvisning til Forvaltningslovens §19)
     * Koden finnes kun i PDL, og ikke i TPS.
     * Routing skjer (foreløpig) basert på TPS, så vi må eksplisitt overstyre enhet.
     * Eksplisitt routing-enhet kan fjernes når Oppgave-APIet går over til PDL.
     */
    STRENGT_FORTROLIG_UTLAND(enhetForAdressebeskyttelse()),

    /** Tidligere spesregkode kode 6 fra TPS  */
    STRENGT_FORTROLIG,

    /** Tidligere spesregkode kode 7 fra TPS  */
    FORTROLIG,

    /** Kode vi kan få fra Folkeregisteret, men brukscaset er ukjent.  */
    UGRADERT, UKJENT;

    fun erGradert(): Boolean {
        return this != UGRADERT && this != UKJENT
    }
}