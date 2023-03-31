package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate

class ReparerArenaDataScheduler(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val arbeidssokerperiodeRepository: ArbeidssokerperiodeRepository,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val leaderElectionClient: LeaderElectionClient
) {

    @Scheduled(initialDelay = 180000, fixedDelay = Long.MAX_VALUE)
    fun avsluttPeriodeBasertPåArenaUttrekk() {
        if (!leaderElectionClient.isLeader) {
            return
        }

        logger.info("Starter jobb for å reparere arbeidssøkerperioder basert på Arena-data")
        val personerMedInaktivDato = lesArenaData()
        var antallAvsluttet = 0
        personerMedInaktivDato.forEach {
            val foedselsnummer = formidlingsgruppeRepository.hentFoedselsnummerForPersonId(it.key)

            if (foedselsnummer.isEmpty()) {
                logger.warn("Fant ikke fødselsnummer for personId ${it.key} - går videre til neste person")
                return@forEach
            }

            if (foedselsnummer.distinct().size > 1) {
                logger.warn("Fant flere ulike fødselsnumre for personId ${it.key} - velger første treff")
            }

            val perioder = arbeidssokerperiodeRepository.hentPerioder(foedselsnummer.first())
                .ifEmpty {
                    logger.info("Fant ingen perioder for person med personId ${it.key} - henter alle fnr i PDL")
                    val alleFnr = pdlOppslagGateway.hentIdenter(foedselsnummer.first(), true)
                    arbeidssokerperiodeRepository.hentPerioder(
                        alleFnr.finnGjeldendeFnr(),
                        alleFnr.finnHistoriskeFoedselsnummer()
                    )
                }

            val aktivPeriode = perioder.find { periode -> periode.til == null }

            if (aktivPeriode == null) {
                logger.info("Fant ikke aktiv periode for personId ${it.key} - går videre til neste person")
                return@forEach
            }

            if (aktivPeriode.fra.toLocalDate().isBefore(it.value)) {
                logger.info("Avslutter periode for personid ${it.key}")
                antallAvsluttet++
                arbeidssokerperiodeRepository.avsluttPeriode(aktivPeriode.foedselsnummer, it.value.atTime(23, 59, 59))
            }
        }

        logger.info("Fullført jobb for å reparere arena-data for ${personerMedInaktivDato.size} personer. Avsluttet $antallAvsluttet perioder.")
    }

    fun lesArenaData(): Map<String, LocalDate> {
        try {
            val fil = this::class.java.classLoader.getResourceAsStream("arena/arena_logglinje.csv")
            fil?.use { inputStream ->
                val reader = inputStream.bufferedReader()
                reader.readLine()
                return reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .map {
                        val (_, inaktiveringsdato, personId) = it.split(',', ignoreCase = false, limit = 3)
                        personId to LocalDate.parse(inaktiveringsdato.substring(0, 10))
                    }.toMap()
            } ?: throw RuntimeException("Klarte ikke lese inn csv-fil med arena-data")
        } catch (e: Exception) {
            throw RuntimeException("Klarte ikke lese inn csv-fil med arena-data", e)
        }
    }
}