package no.nav.fo.veilarbregistrering.postgres.migrering

import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("gcp")
class MigrationStatusService(
    private val migrateClient: MigrateClient,
    private val migrateRepository: MigrateRepository
) {

    fun compareDatabaseStatus(): List<Tabellsjekk> {
        val kilde = TabellNavn.values()
            .associate { it to migrateClient.hentSjekkerForTabell(it)[0] }

        val destinasjon = TabellNavn.values()
            .associate { it to migrateRepository.hentSjekkerForTabell(it)[0] }

        logger.info("Hentet statuser fra veilarbregistrering: $kilde")
        logger.info("Hentet status fra lokal database: $destinasjon")

        return kilde.map { (tabell, resultat) ->

            val kolonnerSomIkkeMatcher: List<String> = resultat.filterNot { (kolonne, verdi) ->
                sjekkSamsvar(verdi, destinasjon[tabell]?.get(kolonne))
            }.keys.toList()

            Tabellsjekk(tabell ,kolonnerSomIkkeMatcher.isEmpty(), kolonnerSomIkkeMatcher)
        }

    }

    companion object {
        private fun sjekkSamsvar(verdiKilde: Any, verdiDestinasjon: Any?): Boolean =
            verdiKilde.toString().toDouble().compareTo(verdiDestinasjon?.toString()?.toDouble() ?: Double.MIN_VALUE) == 0
    }

}

