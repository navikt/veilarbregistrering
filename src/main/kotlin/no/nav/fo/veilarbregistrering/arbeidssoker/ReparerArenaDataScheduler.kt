package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate

class ReparerArenaDataScheduler(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val arbeidssokerperiodeRepository: ArbeidssokerperiodeRepository,
    private val leaderElectionClient: LeaderElectionClient
) {

    @Scheduled(initialDelay = 180000, fixedDelay = Long.MAX_VALUE)
    fun avsluttPeriodeBasertPåArenaUttrekk() {
        if (!leaderElectionClient.isLeader) {
            return
        }

        val personerMedInaktivDato = lesArenaData()
        personerMedInaktivDato.forEach {
            val foedselsnummer = formidlingsgruppeRepository.hentFoedselsnummerForPersonId(it.key)

            if (foedselsnummer == null) {
                logger.warn("Fant ikke fødselsnummer for personId ${it.key} - går videre til neste person")
                return@forEach
            }

            val aktivPeriode = arbeidssokerperiodeRepository.hentPerioder(foedselsnummer)
                .find { periode -> periode.til == null }

            if (aktivPeriode == null) {
                logger.warn("Fant ikke aktiv periode for personId ${it.key} - går videre til neste person")
                return@forEach
            }

            if (aktivPeriode.fra.toLocalDate().isBefore(it.value)) {
                arbeidssokerperiodeRepository.avsluttPeriode(foedselsnummer, it.value.atTime(23, 59, 59))
            }
        }

        logger.info("Fullført jobb for å reparere arena-data for ${personerMedInaktivDato.size} personer")
    }

    private fun lesArenaData(): Map<String, LocalDate> {
        try {
            val fil = this::class.java.classLoader.getResourceAsStream("arena/arena_person.csv")
            fil?.use { inputStream ->
                val reader = inputStream.bufferedReader()
                reader.readLine()
                return reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .map {
                        val (personId, _, sistInaktiv) = it.split(',', ignoreCase = false, limit = 3)
                        personId to LocalDate.parse(sistInaktiv)
                    }.toMap()
            } ?: throw RuntimeException("Klarte ikke lese inn csv-fil med arena-data")
        } catch (e: Exception) {
            throw RuntimeException("Klarte ikke lese inn csv-fil med arena-data", e)
        }
    }
}