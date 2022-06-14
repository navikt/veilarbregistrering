package no.nav.fo.veilarbregistrering.arbeidssoker

class ArbeidssokerperiodeService(
    private val arbeidssokerperiodeProducer: ArbeidssokerperiodeProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        arbeidssokerperioder: Arbeidssokerperioder
    ) {
        if (erAvslutningAvArbeidssokerperiode(endretFormidlingsgruppeCommand, arbeidssokerperioder)) {
            arbeidssokerperiodeProducer.publiserArbeidssokerperiodeAvsluttet(endretFormidlingsgruppeCommand)
        }
    }

    private fun erAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        arbeidssokerperioder: Arbeidssokerperioder
    ): Boolean {
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
        sistePeriode.formidlingsgruppe.kode == "ARBS" && sistePeriode.periode.til == null
}