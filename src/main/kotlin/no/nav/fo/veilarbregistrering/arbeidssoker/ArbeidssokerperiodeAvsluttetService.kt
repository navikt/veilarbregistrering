package no.nav.fo.veilarbregistrering.arbeidssoker

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        arbeidssokerperioder: Arbeidssokerperioder
    ) {
        if (erAvslutningAvArbeidssokerperiode(endretFormidlingsgruppeCommand, arbeidssokerperioder)) {
            val sistePeriode = arbeidssokerperioder.eldsteFoerst().last()
            arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(endretFormidlingsgruppeCommand, sistePeriode)
        }
    }

    private fun erAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        arbeidssokerperioder: Arbeidssokerperioder
    ): Boolean {
        if (arbeidssokerperioder.asList().isEmpty()) return false
        endretFormidlingsgruppeCommand.foedselsnummer?.let {
            val sistePeriode = arbeidssokerperioder.eldsteFoerst().last()
            if (harNaavaerendePeriodeMedARBS(sistePeriode) && endretFormidlingsgruppeCommand.formidlingsgruppe.kode != "ARBS") {
                return true
            }
            return false
        }
        return false
    }

    private fun harNaavaerendePeriodeMedARBS(sistePeriode: Arbeidssokerperiode): Boolean =
        sistePeriode.formidlingsgruppe.kode == "ARBS" && sistePeriode.erGjeldende()
}