package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppeperiode
import no.nav.fo.veilarbregistrering.bruker.Periode

class ArbeidssokerperiodeTidslinje(private val arbeidssokerperioder: List<Formidlingsgruppeperiode>) : Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> =
        arbeidssokerperioder.map(::ArbeidssokerperiodeTidslinjeElement)

    private class ArbeidssokerperiodeTidslinjeElement(private var formidlingsgruppeperiode: Formidlingsgruppeperiode) : TidslinjeElement {
        override fun periode(): Periode {
            return formidlingsgruppeperiode.periode
        }

        override fun type(): Type {
            return Type.ARBEIDSSOKERPERIODE
        }

        override fun kilde(): Kilde {
            return Kilde.ARENA
        }
    }
}
