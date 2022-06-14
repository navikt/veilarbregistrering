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
            if (sistePeriode.formidlingsgruppe.kode == "ARBS" && endretFormidlingsgruppeCommand.formidlingsgruppe.kode !== "ARBS") {
                return true
            }
            return false
        }
        return false
    }
}