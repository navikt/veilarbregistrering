package no.nav.fo.veilarbregistrering.oppfolging

class AktiverBrukerException(val aktiverBrukerFeil: AktiverBrukerFeil) : RuntimeException("Feil ved aktivering av bruker")
