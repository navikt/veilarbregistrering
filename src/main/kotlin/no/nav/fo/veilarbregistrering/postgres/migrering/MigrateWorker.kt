package no.nav.fo.veilarbregistrering.postgres.migrering

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Profile("gcp")
class MigrateWorker(@Autowired val leaderElectionClient: LeaderElectionClient, @Autowired val repository: MigrateRepository, @Autowired val migrateClient: MigrateClient) {
    @Scheduled(fixedDelay = 20000)
    fun migrate() {
        // Lese pg-db, finne tabeller og kolonnenavn
        // Autorisasjon (header som leses) - hentes i veilarbregistrering fra Google secret manager
        // Proxy-er
        if (isOnPrem()) {
            logger.warn("Migreringsjobb for GCP ble forsøkt kjørt fra FSS")
            return
        }

        if (!leaderElectionClient.isLeader) {
            return
        }

        TabellNavn.values().forEach {
            val sisteIndex = repository.hentStørsteId(it)
            val rader = migrateClient.hentNesteBatchFraTabell(it, sisteIndex)
            repository.settInnRader(it, rader)
        }

        val antallSomKanTrengeOppdatering = repository.antallRaderSomKanTrengeOppdatering()
        if (migrateClient.hentAntallPotensieltOppdaterteTilstander() != antallSomKanTrengeOppdatering) {
            hentOgOppdaterRegistreringTilstander()
        }
    }

    private fun hentOgOppdaterRegistreringTilstander() {
        val trengerOppdatering = repository.hentRaderSomKanTrengeOppdatering()

        val rader = migrateClient.hentOppdaterteRegistreringStatuser(trengerOppdatering)

        logger.info("Hentet oppdaterte rader:", rader)
        val antallOppdaterte = repository.oppdaterTilstander(rader)

        if (rader.size == antallOppdaterte.size) logger.info("Oppdaterte ${antallOppdaterte.size} rader")
        else logger.warn("Oppdaterte ${antallOppdaterte.size} rader, men mottok ${rader.size} fra oracle")
    }
}
