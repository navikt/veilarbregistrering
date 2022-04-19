package no.nav.fo.veilarbregistrering.metrics

interface Metric {
    fun fieldName(): String
    fun value(): Any

    companion object {
        fun of(fieldName: String, value: Any) = object:Metric {
            override fun fieldName() = fieldName
            override fun value() = value
        }
    }
}