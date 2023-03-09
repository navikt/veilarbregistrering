package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheService
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class FormidlingsgruppeMottakService(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val aktorIdCacheService: AktorIdCacheService,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService
) {

    @Transactional
    fun behandle(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {

        if(vaskFormidlingsgruppeEventOgStopp(formidlingsgruppeEndretEvent)) return

        try {
            aktorIdCacheService.hentAktorIdFraPDLHvisIkkeFinnes(formidlingsgruppeEndretEvent.foedselsnummer, true)
        } catch (e: Exception) {
            logger.warn("Klarte ikke populere aktørid-cache for innkommende formidlingsgruppe", e)
        }

        try {
            arbeidssokerperiodeService.behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent)
        } catch (e: Exception) {
            logger.error("Feil ved behandling av formidlingsgruppe event", e)
        }

        formidlingsgruppeRepository.lagre(formidlingsgruppeEndretEvent)
    }

    private fun vaskFormidlingsgruppeEventOgStopp(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent): Boolean {
        if (formidlingsgruppeEndretEvent.formidlingsgruppeEndret.isBefore(LocalDateTime.parse("2010-01-01T00:00:00"))
            && formidlingsgruppeEndretEvent.formidlingsgruppe.kode != "ARBS") {
            logger.warn(
                "Fikk formidlingsgruppeendring fra før 2010 som ikke har formidlingsgruppe ARBS, " +
                        "formidlingsgruppe: ${formidlingsgruppeEndretEvent.formidlingsgruppe.kode}, " +
                        "dato: ${formidlingsgruppeEndretEvent.formidlingsgruppeEndret}) ")
        }

        if (formidlingsgruppeEndretEvent.operation != Operation.UPDATE) {
            logger.info("Forkaster melding som ikke er UPDATE, men lagrer for ettertid - $formidlingsgruppeEndretEvent")
            if (formidlingsgruppeEndretEvent.formidlingsgruppe.kode != "ISERV") {
                logger.error("Mottok en INSERT-melding med formidlingsgruppe ${formidlingsgruppeEndretEvent.formidlingsgruppe} - vi skal kun få INSERT med ISERV")
            }
            formidlingsgruppeRepository.lagre(formidlingsgruppeEndretEvent)
            return true
        }
        return false
    }
}
