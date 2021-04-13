package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.bruker.Periode
import kotlin.streams.toList

class ArbeidssokerperiodeTidslinje(private val arbeidssokerperioder: List<Arbeidssokerperiode>) : Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> {
        return arbeidssokerperioder.stream().map(::ArbeidssokerperiodeTidslinjeElement).toList()
    }

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
