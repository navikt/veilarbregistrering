package no.nav.fo.veilarbregistrering.arbeidssoker

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        formidlingsgruppeperioder: Arbeidssokerperioder
    ) {
        if (erAvslutningAvArbeidssokerperiode(endretFormidlingsgruppeCommand, formidlingsgruppeperioder)) {
            val sistePeriode = formidlingsgruppeperioder.eldsteFoerst().last()
            arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(endretFormidlingsgruppeCommand, sistePeriode)
        }
    }

    private fun erAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        formidlingsgruppeperioder: Arbeidssokerperioder
    ): Boolean {
        if (formidlingsgruppeperioder.asList().isEmpty()) return false
        endretFormidlingsgruppeCommand.foedselsnummer?.let {
            val sistePeriode = formidlingsgruppeperioder.eldsteFoerst().last()
            if (harNaavaerendePeriodeMedARBS(sistePeriode) && endretFormidlingsgruppeCommand.formidlingsgruppe.kode != "ARBS") {
                return true
            }
            return false
        }
        return false
    }

    private fun harNaavaerendePeriodeMedARBS(sistePeriode: Formidlingsgruppeperiode): Boolean =
        sistePeriode.formidlingsgruppe.kode == "ARBS" && sistePeriode.periode.til == null
}