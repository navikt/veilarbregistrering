package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import org.springframework.scheduling.annotation.Scheduled

class PopulerFoedselsnummerScheduler(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
    private val leaderElectionClient: LeaderElectionClient,
    private val unleashClient: UnleashClient
) {

    @Scheduled(initialDelay = 1000 * 60 * 2, fixedDelay = Long.MAX_VALUE)
    fun populerFoedselsnummer() {
        if (isOnPrem()) throw IllegalStateException("populerFoedselsnummer skal ikke benyttes i OnPrem")

        logger.info("Starter populering av foedselsnummer")

        if (!leaderElectionClient.isLeader) {
            logger.info("Er ikke leader - avbryter")
            return
        }

        val denyList = mutableListOf<AktorId>()
        var rowsUpdated = 1
        var totalRowsUpdated = 0

        while (rowsUpdated != 0 && unleashClient.isEnabled("veilarbregistrering.populerFoedselsnummer")) {
            val t1 = System.currentTimeMillis()
            logger.info("Forsøker å finne sykmeldtRegistreringer som mangler foedselsnummer for populering...")

            val aktorIdList =
                sykmeldtRegistreringRepository.finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(50, denyList)
            logger.info("Fant ${aktorIdList.size} tilfeller av aktorId som manglet fødselsnummer")

            if (aktorIdList.isEmpty()) break

            val aktorIdFoedselsnummerMap = pdlOppslagGateway.hentIdenterBolk(aktorIdList)
            if (aktorIdFoedselsnummerMap.isEmpty()) {
                logger.info("Fant ingen identer fra hentIdenterBolk")
                break
            }

            val aktorIdsDenied = aktorIdList.subtract(aktorIdFoedselsnummerMap.keys)
            logger.info("Disse aktørId'ene fikk ikke treff i PDL, og er lagt til i denylist: $aktorIdsDenied")

            denyList.addAll(aktorIdsDenied)

            val oppdaterteSykmeldtRegistreringer =
                sykmeldtRegistreringRepository.oppdaterSykmeldtRegistreringerMedManglendeFoedselsnummer(
                    aktorIdFoedselsnummerMap,
                )
            rowsUpdated = oppdaterteSykmeldtRegistreringer.toList().sum()
            totalRowsUpdated += rowsUpdated
            logger.info("Oppdaterte ${rowsUpdated} SykmeldtRegistrering ila ${System.currentTimeMillis() - t1} ms")
        }

        logger.info("Avslutter populering av Foedselsnummer da det ikke var flere kjente aktørIder. " +
                "Oppdaterte totalt ${totalRowsUpdated} SykmeldtRegistreringer. " +
                "Fant totalt ${denyList.size} aktorIder som ikke gav treff i PDL")
    }
}