package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import org.springframework.scheduling.annotation.Scheduled
import kotlin.math.log

class PopulerFoedselsnummerScheduler(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository) {

    @Scheduled(initialDelay = 1000 * 30, fixedDelay = Long.MAX_VALUE)
    fun populerFoedselsnummer() {
        if (isProduction()) return

        logger.info("Forsøker å finne sykmeldtRegistreringer som mangler foedselsnummer for populering...")

        val aktorIdList =
            sykmeldtRegistreringRepository.finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(50)

        logger.info("Fant ${aktorIdList.size} tilfeller av aktorId som manglet fødselsnummer")

        val foedselsnummer = pdlOppslagGateway.hentIdenterBolk(aktorIdList)
        logger.info("Fikk følgende respons fra hentIdenterBolk: $foedselsnummer")
    }
}