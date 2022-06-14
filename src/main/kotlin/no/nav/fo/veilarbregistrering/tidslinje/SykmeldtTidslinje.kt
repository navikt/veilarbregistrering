package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering
import kotlin.streams.toList

class SykmeldtTidslinje(private var sykmeldtRegistreringer: List<SykmeldtRegistrering>) : Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> {
        return sykmeldtRegistreringer.stream().map(::SykmeldtTidslinjeElement).toList()
    }

    private class SykmeldtTidslinjeElement(private var sykmeldtRegistrering: SykmeldtRegistrering) : TidslinjeElement {
        override fun periode(): Periode {
            return Periode.gyldigPeriode(sykmeldtRegistrering.opprettetDato.toLocalDate(), null)
        }

        override fun type(): Type {
            return Type.SYKMELDT_REGISTRERT
        }

        override fun kilde(): Kilde {
            return Kilde.ARBEIDSSOKERREGISTRERING
        }
    }
}

