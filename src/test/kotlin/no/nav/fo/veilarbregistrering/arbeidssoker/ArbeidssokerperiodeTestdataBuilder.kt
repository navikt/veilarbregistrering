package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode
import java.time.LocalDate

class ArbeidssokerperiodeTestdataBuilder private constructor() :
    Builder<Arbeidssokerperiode> {
    private var fra: LocalDate? = null
    private var til: LocalDate? = null
    override fun build(): Arbeidssokerperiode {
        return Arbeidssokerperiode(Formidlingsgruppe("ARBS"), Periode.gyldigPeriode(fra, til))
    }

    fun fra(fra: LocalDate?): ArbeidssokerperiodeTestdataBuilder {
        this.fra = fra
        return this
    }

    fun til(til: LocalDate?): ArbeidssokerperiodeTestdataBuilder {
        this.til = til
        return this
    }

    companion object {
        fun arbeidssokerperiode(): ArbeidssokerperiodeTestdataBuilder {
            return ArbeidssokerperiodeTestdataBuilder()
        }
    }
}
