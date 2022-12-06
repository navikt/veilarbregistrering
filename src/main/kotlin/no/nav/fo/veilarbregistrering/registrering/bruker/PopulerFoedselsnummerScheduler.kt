package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import org.springframework.scheduling.annotation.Scheduled

class PopulerFoedselsnummerScheduler(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
    private val leaderElectionClient: LeaderElectionClient) {

    @Scheduled(initialDelay = 1000 * 60 * 2, fixedDelay = Long.MAX_VALUE)
    fun populerFoedselsnummer() {
        if (isProduction()) return
        logger.info("Starter populering av foedselsnummer")

        if (!leaderElectionClient.isLeader) {
            logger.info("Er ikke leader - avbryter")
            return
        }
        logger.info("Forsøker å finne sykmeldtRegistreringer som mangler foedselsnummer for populering...")

        val aktorIdList =
            sykmeldtRegistreringRepository.finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(10)
        logger.info("Fant ${aktorIdList.size} tilfeller av aktorId som manglet fødselsnummer")

        if (aktorIdList.isEmpty()) return

        val aktorIdFoedselsnummerMap = pdlOppslagGateway.hentIdenterBolk(aktorIdList)
        logger.info("Fikk følgende respons fra hentIdenterBolk: $aktorIdFoedselsnummerMap")

        val oppdaterteSykmeldtRegistreringer =
            sykmeldtRegistreringRepository.oppdaterSykmeldtRegistreringerMedManglendeFoedselsnummer(
                aktorIdFoedselsnummerMap
            )

        logger.info("Oppdaterte totalt $oppdaterteSykmeldtRegistreringer SykmeldtRegistrering")
    }
}