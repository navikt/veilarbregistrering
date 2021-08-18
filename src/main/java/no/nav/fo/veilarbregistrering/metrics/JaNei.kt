package no.nav.fo.veilarbregistrering.metrics

enum class JaNei(val verdi: Boolean) : Metric {
    JA(true),
    NEI(false);

    override fun fieldName() = "svar"
    override fun value() = this.verdi.toString()
}