package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperiodeAvsluttetService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class FormidlingsgruppeMottakService(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val arbeidssokerperiodeAvsluttetService: ArbeidssokerperiodeAvsluttetService
) {

    @Transactional
    fun behandle(formidlingsgruppeEvent: FormidlingsgruppeEvent) {

        if (formidlingsgruppeEvent.formidlingsgruppeEndret.isBefore(LocalDateTime.parse("2010-01-01T00:00:00"))
            && formidlingsgruppeEvent.formidlingsgruppe.kode != "ARBS"){
            logger.warn(
                "Fikk formidlingsgruppeendring fra før 2010 som ikke har formidlingsgruppe ARBS, " +
                        "formidlingsgruppe: ${formidlingsgruppeEvent.formidlingsgruppe.kode}, " +
                        "dato: ${formidlingsgruppeEvent.formidlingsgruppeEndret}) ")
        }

        val eksisterendeArbeidssokerperioderLokalt = formidlingsgruppeRepository.finnFormidlingsgrupperOgMapTilArbeidssokerperioder(
            listOf(formidlingsgruppeEvent.foedselsnummer)
        )

        formidlingsgruppeRepository.lagre(formidlingsgruppeEvent)

        arbeidssokerperiodeAvsluttetService.behandleAvslutningAvArbeidssokerperiode(
            formidlingsgruppeEvent,
            eksisterendeArbeidssokerperioderLokalt
        )
    }
}