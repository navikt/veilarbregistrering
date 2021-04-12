package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import kotlin.streams.toList

class OrdinaerRegistreringTidslinje(private var ordinaereBrukerRegistreringer: List<OrdinaerBrukerRegistrering>) : Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> {
        return ordinaereBrukerRegistreringer.stream().map(::OrdinaerRegistreringTidslinjeElement).toList()
    }

    private class OrdinaerRegistreringTidslinjeElement(private var ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering) : TidslinjeElement {
        override fun periode(): Periode {
            return Periode.gyldigPeriode(ordinaerBrukerRegistrering.opprettetDato.toLocalDate(), null)
        }

        override fun type(): Type {
            return Type.ORDINAER_REGISTRERING
        }

        override fun kilde(): Kilde {
            return Kilde.ARBEIDSSOKERREGISTRERING
        }
    }
}

