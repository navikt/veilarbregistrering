package no.nav.fo.veilarbregistrering.tidslinje

import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering
import kotlin.streams.toList

class SykmeldtTidslinje(private var sykmeldtRegistreringer: List<SykmeldtRegistrering>) : Tidslinje {

    override fun tidslinje(): List<TidslinjeElement> {
        return sykmeldtRegistreringer.stream().map(::SykmeldtTidslinjeElement).toList()
    }

    private class SykmeldtTidslinjeElement(private var sykmeldtRegistrering: SykmeldtRegistrering) : TidslinjeElement {
        override fun periode(): Periode {
            return Periode.gyldigPeriode(sykmeldtRegistrering.opprettetDato.toLocalDate(), null)
        }

        override fun status(): Status {
            return Status.SYKMELDT_REGISTRERT
        }

        override fun kilde(): Kilde {
            return Kilde.ARBEIDSSOKERREGISTRERING
        }
    }
}

