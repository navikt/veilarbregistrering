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
        val sisteMeldekort = meldekortService.hentSisteMeldekort(fnr)

        if (sisteMeldekort != null) {
            val sendtSiste14Dager = sisteMeldekort.erSendtInnSiste14Dager()

            metricsService.registrer(
                Events.AVSLUTNING_PERIODE_MELDEKORT,
                Tag.of("erArbeidssokerNestePeriode", sisteMeldekort.erArbeidssokerNestePeriode.toString()),
                Tag.of("sendtSiste14Dager", sendtSiste14Dager.toString()),
                Tag.of("harInnsendteMeldekort", "true")
            )
        } else {
            metricsService.registrer(
                Events.AVSLUTNING_PERIODE_MELDEKORT,
                Tag.of("harInnsendteMeldekort", "false")
            )
        }
    }
}
