package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import org.springframework.scheduling.annotation.Scheduled

class PopulerFoedselsnummerScheduler(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
    private val leaderElectionClient: LeaderElectionClient
) {

    @Scheduled(initialDelay = 1000 * 60 * 2, fixedDelay = Long.MAX_VALUE)
    fun populerFoedselsnummer() {
        if (isProduction()) return
        logger.info("Starter populering av foedselsnummer")

        if (!leaderElectionClient.isLeader) {
            logger.info("Er ikke leader - avbryter")
            return
        }

        val denyList = mutableListOf<AktorId>()

        var totalRowsUpdated = 1

        while (totalRowsUpdated != 0) {
            logger.info("Forsøker å finne sykmeldtRegistreringer som mangler foedselsnummer for populering...")

            val aktorIdList =
                sykmeldtRegistreringRepository.finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(50, denyList)
            logger.info("Fant ${aktorIdList.size} tilfeller av aktorId som manglet fødselsnummer")

            if (aktorIdList.isEmpty()) return

            val aktorIdFoedselsnummerMap = pdlOppslagGateway.hentIdenterBolk(aktorIdList)
            if (aktorIdFoedselsnummerMap.isEmpty()) {
                logger.info("Fant ingen identer fra hentIdenterBolk")
                return
            }
            logger.info("Fikk følgende respons fra hentIdenterBolk: $aktorIdFoedselsnummerMap")

            val aktorIdsDenied = aktorIdList.subtract(aktorIdFoedselsnummerMap.keys)
            logger.info("Disse aktørId'ene fikk ikke treff i PDL, og er lagt til i denylist: $aktorIdsDenied")

            denyList.addAll(aktorIdsDenied)

            val oppdaterteSykmeldtRegistreringer =
                sykmeldtRegistreringRepository.oppdaterSykmeldtRegistreringerMedManglendeFoedselsnummer(
                    aktorIdFoedselsnummerMap,
                )
            totalRowsUpdated = oppdaterteSykmeldtRegistreringer.toList().sum()
            logger.info("Oppdaterte totalt ${totalRowsUpdated} SykmeldtRegistrering")
        }
    }
}