package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.registrering.bruker.Reaktivering
import kotlin.streams.toList

class ReaktiveringTidslinje(private val reaktiveringer: List<Reaktivering>): Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> {
        return reaktiveringer.stream().map(::ReaktiveringTidslinjeElement).toList()
    }

    private class ReaktiveringTidslinjeElement(private var reaktivering: Reaktivering) : TidslinjeElement {
        override fun periode(): Periode {
            return Periode.gyldigPeriode(reaktivering.opprettetTidspunkt.toLocalDate(), null)
        }

        override fun type(): Type {
            return Type.REAKTIVERING
        }

        override fun kilde(): Kilde {
            return Kilde.ARBEIDSSOKERREGISTRERING
        }
    }

}
