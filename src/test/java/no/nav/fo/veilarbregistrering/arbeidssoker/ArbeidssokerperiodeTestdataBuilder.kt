package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Periode
import java.time.LocalDate

class ArbeidssokerperiodeTestdataBuilder private constructor(private val formidlingsgruppe: Formidlingsgruppe) :
    Builder<Arbeidssokerperiode> {
    private var fra: LocalDate? = null
    private var til: LocalDate? = null
    override fun build(): Arbeidssokerperiode {
        return Arbeidssokerperiode(formidlingsgruppe, Periode.of(fra, til))
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
        @JvmStatic
        fun medArbs(): ArbeidssokerperiodeTestdataBuilder {
            return ArbeidssokerperiodeTestdataBuilder(Formidlingsgruppe.of("ARBS"))
        }

        @JvmStatic
        fun medIserv(): ArbeidssokerperiodeTestdataBuilder {
            return ArbeidssokerperiodeTestdataBuilder(Formidlingsgruppe.of("ISERV"))
        }
    }
}
