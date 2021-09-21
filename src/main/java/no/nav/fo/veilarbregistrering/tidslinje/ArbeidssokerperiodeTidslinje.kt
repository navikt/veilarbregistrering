package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.bruker.Periode

class ArbeidssokerperiodeTidslinje(private val arbeidssokerperioder: List<Arbeidssokerperiode>) : Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> =
        arbeidssokerperioder.map(::ArbeidssokerperiodeTidslinjeElement)

    private class ArbeidssokerperiodeTidslinjeElement(private var arbeidssokerperiode: Arbeidssokerperiode) : TidslinjeElement {
        override fun periode(): Periode {
            return arbeidssokerperiode.periode
        }

        override fun type(): Type {
            return Type.ARBEIDSSOKERPERIODE
        }

        override fun kilde(): Kilde {
            return Kilde.ARENA
        }
    }
}
