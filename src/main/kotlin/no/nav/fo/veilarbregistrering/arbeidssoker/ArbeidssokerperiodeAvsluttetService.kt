package no.nav.fo.veilarbregistrering.arbeidssoker

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        arbeidssokerperioder: Arbeidssokerperioder
    ) {
        endretFormidlingsgruppeCommand.foedselsnummer?.let {
            arbeidssokerperioder.nyestePeriode()?.let {
                if (it.erGjeldende() && endretFormidlingsgruppeCommand.formidlingsgruppe.kode != "ARBS") {
                    arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(
                        endretFormidlingsgruppeCommand,
                        it
                    )
                }
            }
        }
    }
}
