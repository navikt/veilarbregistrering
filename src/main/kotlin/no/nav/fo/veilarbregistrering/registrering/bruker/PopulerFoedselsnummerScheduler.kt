package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.scheduling.annotation.Scheduled

class PopulerFoedselsnummerScheduler(private val pdlOppslagGateway: PdlOppslagGateway) {

    @Scheduled(initialDelay = 1000 * 30, fixedDelay = Long.MAX_VALUE)
    fun populerFoedselsnummer() {
        if (isProduction()) return

        val foedselsnummer = pdlOppslagGateway.hentIdenterBolk(listOf(AktorId("2057713142949"), AktorId("2877772301361")))
        logger.info("Fikk følgende respons fra hentIdenterBolk: $foedselsnummer")
    }
}