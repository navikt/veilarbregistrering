package no.nav.fo.veilarbregistrering.registrering.bruker

class AktiverBrukerException(val aktiverBrukerFeil: AktiverBrukerFeil) : RuntimeException("Feil ved aktivering av bruker")
