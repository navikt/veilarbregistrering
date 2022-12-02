package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperiodeAvsluttetService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class FormidlingsgruppeMottakService(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val arbeidssokerperiodeAvsluttetService: ArbeidssokerperiodeAvsluttetService,
    private val unleashClient: UnleashClient
) {

    @Transactional
    fun behandle(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {

        if (formidlingsgruppeEndretEvent.formidlingsgruppeEndret.isBefore(LocalDateTime.parse("2010-01-01T00:00:00"))
            && formidlingsgruppeEndretEvent.formidlingsgruppe.kode != "ARBS"){
            logger.warn(
                "Fikk formidlingsgruppeendring fra før 2010 som ikke har formidlingsgruppe ARBS, " +
                        "formidlingsgruppe: ${formidlingsgruppeEndretEvent.formidlingsgruppe.kode}, " +
                        "dato: ${formidlingsgruppeEndretEvent.formidlingsgruppeEndret}) ")
        }

        val skalUtledeAvslutningAvPeriode = unleashClient.isEnabled("veilarbregistrering.utled-avslutning-arbeidssokerperiode")
        val eksisterendeArbeidssokerperioderLokalt = if (skalUtledeAvslutningAvPeriode) formidlingsgruppeRepository.finnFormidlingsgrupperOgMapTilArbeidssokerperioder(
            listOf(formidlingsgruppeEndretEvent.foedselsnummer)
        ) else null

        formidlingsgruppeRepository.lagre(formidlingsgruppeEndretEvent)

        if (skalUtledeAvslutningAvPeriode) {
            arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(
                formidlingsgruppeEndretEvent,
                eksisterendeArbeidssokerperioderLokalt!!
            )
        }
    }
}