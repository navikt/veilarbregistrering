package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.secureLogger
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

class PopulerAktorIdWorker(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val aktorIdCacheRepository: AktorIdCacheRepository,
    private val unleashClient: UnleashClient,
    private val leaderElectionClient: LeaderElectionClient
) {
    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 180000)
    fun populereAktorId() {
        if (!leaderElectionClient.isLeader) {
            return
        }
        if (isProduction()) {
            return
        }
        logger.info("Startet jobb for å populere aktor_id_cache")
        var foedselsnummer: List<Foedselsnummer> = formidlingsgruppeRepository.hentUnikeFoedselsnummer().distinct()
        logger.info("Hentet ${foedselsnummer.size} unike foedselsnummer")
        var teller = 1
        /*while (foedselsnummer.isNotEmpty() && unleashClient.isEnabled("veilarbregistrering.populere-aktorid")) {
            val foedselsnummerBolk = foedselsnummer.take(100)
            foedselsnummer = foedselsnummer.drop(100)

            val aktorIdFnrMap = pdlOppslagGateway.hentIdenterBolk(foedselsnummerBolk, true)
            val fnrUtenTreff = foedselsnummerBolk.subtract(aktorIdFnrMap.keys)

            if (fnrUtenTreff.isNotEmpty()) {
                secureLogger.warn("Aktor_id ikke funnet for foedselsnummer $fnrUtenTreff i bolk $teller")
                logger.info("${fnrUtenTreff.size} fødselsnummer manglet aktorId i PDL for bolk nr $teller")
            }
            if (aktorIdFnrMap.isEmpty()) {
                teller += 1
                logger.info("Fant ingen identer fra hentIdenterBolk i bolk nr $teller")
                continue
            }

            val oppdaterteRader =
                aktorIdCacheRepository.lagreBolk(aktorIdFnrMap.map {
                    AktorIdCache(
                        it.key,
                        it.value,
                        LocalDateTime.now()
                    )
                })

            logger.info("Oppdaterte $oppdaterteRader i jobb som populerer AktørId-cache for bolk nr $teller")
            teller += 1
        }*/
        logger.info("Jobb for å populere aktor_id_cache er nå fullført")
    }

}