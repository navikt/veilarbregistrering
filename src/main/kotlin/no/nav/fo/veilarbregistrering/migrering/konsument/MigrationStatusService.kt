package no.nav.fo.veilarbregistrering.migrering.konsument

import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.TabellNavn

class MigrationStatusService(
    private val migrateClient: MigrateClient,
    private val migrateRepository: MigrateRepository
) {

    fun compareDatabaseStatus(): List<Tabellsjekk> {
        logger.info("compareDatabaseStatus")
        val kilde = TabellNavn.values().associate { it to migrateClient.hentSjekkerForTabell(it)[0] }
        val destinasjon = TabellNavn.values().associate { it to migrateRepository.hentSjekkerForTabell(it)[0] }

        logger.info("Hentet statuser fra FSS: $kilde")
        logger.info("Hentet statuser fra GCP: $destinasjon")

        return kilde.map { (tabell, resultat) ->
            val kolonnerSomIkkeMatcher: List<String> = resultat.filterNot { (kolonne, verdi) ->
                sjekkSamsvar(verdi, destinasjon[tabell]?.get(kolonne))
            }.keys.toList()

            Tabellsjekk(tabell ,kolonnerSomIkkeMatcher.isEmpty(), kolonnerSomIkkeMatcher)
        }
    }

    fun compareProfilering(): List<Long> {
        logger.info("Sammenligner profilering")

        val gcpProfileringer = migrateRepository.hentSjekkerForProfilering()
        val fssProfileringer = migrateClient.hentSjekkerForProfilering()

        val profileringerIkkeIGcp = fssProfileringer.subtract(gcpProfileringer.toSet())

        logger.info("Registreringer med profilering som ikke finnes i GCP: $profileringerIkkeIGcp")
        return profileringerIkkeIGcp.toList()
    }

    companion object {
        private fun sjekkSamsvar(verdiKilde: Any, verdiDestinasjon: Any?): Boolean =
            verdiKilde.toString().toDouble().compareTo(verdiDestinasjon?.toString()?.toDouble() ?: Double.MIN_VALUE) == 0
    }
}
