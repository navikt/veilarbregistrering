package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import java.time.LocalDate

class TidslinjeTestdataBuilder {
    companion object {
        fun default(): Tidslinje = OrdinaerRegistreringTidslinje(listOf(gyldigBrukerRegistrering(LocalDate.of(2021, 4, 11).atStartOfDay())))
    }
}
