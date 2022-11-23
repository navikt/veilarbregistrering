package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortService
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService

class ArbeidssokerperiodeAvsluttetService(
    private val arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer,
    private val meldekortService: MeldekortService,
    private val metricsService: MetricsService
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

                sammenlignAvslutningMedMeldekortData(formidlingsgruppeEndretEvent.foedselsnummer)
            }
        }
    }

    private fun sammenlignAvslutningMedMeldekortData(fnr: Foedselsnummer) {
        val meldekort = meldekortService.hentSisteMeldekort(fnr)

        //TODO: Metrikk på de som har avslutning og ingen meldekort - da er avslutningen trigget av veileder i Arena eller ikke innsendt meldekort
        meldekort?.let {
            if (meldekort.erArbeidssokerNestePeriode) {
                metricsService.registrer(Events.AVSLUTNING_VIA_MELDEKORT, Tag.of("erArbeidssokerNestePeriode", "true"))
            } else {
                metricsService.registrer(Events.AVSLUTNING_VIA_MELDEKORT, Tag.of("erArbeidssokerNestePeriode", "false"))
            }
        }
    }
}
