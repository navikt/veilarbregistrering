package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer
) {

    fun behandleAvslutningAvArbeidssokerperiode(
        formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent,
        arbeidssokerperioder: Arbeidssokerperioder
    ) {
        arbeidssokerperioder.nyestePeriode()?.let {
            if (it.erGjeldende() && formidlingsgruppeEndretEvent.formidlingsgruppe.kode != "ARBS") {
                arbeidssokerperiodeAvsluttetProducer.publiserArbeidssokerperiodeAvsluttet(
                    formidlingsgruppeEndretEvent,
                    it
                )
            }
        }
    }
}
