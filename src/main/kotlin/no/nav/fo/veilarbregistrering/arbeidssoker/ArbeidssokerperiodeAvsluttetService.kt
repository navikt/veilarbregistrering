package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEvent

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        formidlingsgruppeEvent: FormidlingsgruppeEvent,
        arbeidssokerperioder: Arbeidssokerperioder
    ) {
        arbeidssokerperioder.nyestePeriode()?.let {
            if (it.erGjeldende() && formidlingsgruppeEvent.formidlingsgruppe.kode != "ARBS") {
                arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(
                    formidlingsgruppeEvent,
                    it
                )
            }
        }
    }
}
