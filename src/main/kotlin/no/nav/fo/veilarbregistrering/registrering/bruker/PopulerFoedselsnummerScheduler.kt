package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.scheduling.annotation.Scheduled

class PopulerFoedselsnummerScheduler(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val populerFoedselsnummerRepository: PopulerFoedselsnummerRepository,
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
        var totalRowsUpdated = 0

        val t0 = System.currentTimeMillis()
        var aktorIdList = populerFoedselsnummerRepository.finnAktorIdTilRegistrertUtenFoedselsnummer()
        logger.info("Fant ${aktorIdList.size} tilfeller av aktorId som manglet fødselsnummer")

        while (aktorIdList.isNotEmpty() && unleashClient.isEnabled("veilarbregistrering.populerFoedselsnummer")) {
            val t1 = System.currentTimeMillis()
            logger.info("Forsøker å finne Ordinære registreringer som mangler foedselsnummer for populering...")

            val aktorIdForIterasjon = aktorIdList.take(100)
            aktorIdList = aktorIdList.drop(100)
            if (aktorIdForIterasjon.isEmpty()) {
                logger.info("Fant ingen flere tilfeller av aktorId som manglet fødselnummer - avbryter")
                break
            }

            val aktorIdFoedselsnummerMap = pdlOppslagGateway.hentIdenterBolk(aktorIdForIterasjon)
            if (aktorIdFoedselsnummerMap.isEmpty()) {
                logger.info("Fant ingen identer fra hentIdenterBolk")
                continue
            }
            val aktorIdsDenied = aktorIdForIterasjon.subtract(aktorIdFoedselsnummerMap.keys)
            if (aktorIdsDenied.isNotEmpty()) {
                logger.warn("Disse aktørId'ene fikk ikke treff i PDL, og er lagt til i denylist: $aktorIdsDenied")
                denyList.addAll(aktorIdsDenied)
            }

            /*
            val oppdaterteRegistreringer =
                populerFoedselsnummerRepository.oppdaterRegistreringerMedManglendeFoedselsnummer(
                    aktorIdFoedselsnummerMap
                )
            val rowsUpdated = oppdaterteRegistreringer.toList().sum()
            totalRowsUpdated += rowsUpdated
            logger.info("Oppdaterte $rowsUpdated Ordinære registreringer ila ${System.currentTimeMillis() - t1} ms")
            *(
             */
        }

        logger.info("Avslutter populering av Foedselsnummer da det ikke var flere kjente aktørIder. " +
                "Oppdaterte totalt $totalRowsUpdated Ordinære registreringer ila ${System.currentTimeMillis() - t0} ms. " +
                "Fant totalt ${denyList.size} aktorIder som ikke gav treff i PDL")
    }
}