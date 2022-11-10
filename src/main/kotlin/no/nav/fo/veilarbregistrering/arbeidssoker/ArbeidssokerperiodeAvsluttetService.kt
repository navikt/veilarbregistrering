package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.EndretFormidlingsgruppeCommand

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand,
        arbeidssokerperioder: Arbeidssokerperioder
    ) {
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
