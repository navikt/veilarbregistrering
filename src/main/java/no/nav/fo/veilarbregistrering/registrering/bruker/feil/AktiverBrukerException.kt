package no.nav.fo.veilarbregistrering.registrering.bruker.feil

import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil

class AktiverBrukerException(val aktiverBrukerFeil: AktiverBrukerFeil) : RuntimeException("Feil ved aktivering av bruker")
