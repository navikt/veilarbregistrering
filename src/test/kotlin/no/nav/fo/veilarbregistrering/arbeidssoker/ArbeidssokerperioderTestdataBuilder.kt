package no.nav.fo.veilarbregistrering.arbeidssoker

class ArbeidssokerperioderTestdataBuilder private constructor() {
    private val arbeidssokerperioder: MutableList<Arbeidssokerperiode>
    fun periode(arbeidssokerperiode: ArbeidssokerperiodeTestdataBuilder): ArbeidssokerperioderTestdataBuilder {
        arbeidssokerperioder.add(arbeidssokerperiode.build())
        return this
    }

    fun build(): Arbeidssokerperioder {
        return Arbeidssokerperioder(arbeidssokerperioder)
    }

    fun arbeidssokerperiode(arbeidssokerperiode: Builder<Arbeidssokerperiode>): ArbeidssokerperioderTestdataBuilder {
        arbeidssokerperioder.add(arbeidssokerperiode.build())
        return this
    }

    companion object {
        fun arbeidssokerperioder(): ArbeidssokerperioderTestdataBuilder {
            return ArbeidssokerperioderTestdataBuilder()
        }
    }

    init {
        arbeidssokerperioder = ArrayList()
    }
}
