package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering

class TidslinjeTestdataBuilder {
    companion object {
        fun default(): Tidslinje = OrdinaerRegistreringTidslinje(listOf(gyldigBrukerRegistrering()))
    }
}
