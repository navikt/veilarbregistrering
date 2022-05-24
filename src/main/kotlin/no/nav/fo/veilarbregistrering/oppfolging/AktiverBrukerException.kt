package no.nav.fo.veilarbregistrering.oppfolging

class AktiverBrukerException(val feilmelding: String, val aktiverBrukerFeil: AktiverBrukerFeil) : RuntimeException(feilmelding)
