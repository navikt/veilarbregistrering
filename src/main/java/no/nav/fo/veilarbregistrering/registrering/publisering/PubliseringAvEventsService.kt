package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class PubliseringAvEventsService(
    private val profileringRepository: ProfileringRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val arbeidssokerRegistrertProducer: ArbeidssokerRegistrertProducer,
    private val arbeidssokerRegistrertProducerAiven: ArbeidssokerRegistrertProducer,
    private val registreringTilstandRepository: RegistreringTilstandRepository,
    private val arbeidssokerProfilertProducer: ArbeidssokerProfilertProducer,
    private val arbeidssokerProfilertProducerAiven: ArbeidssokerProfilertProducer,
    private val prometheusMetricsService: PrometheusMetricsService
) {
    @Transactional
    fun publiserEvents() {
        rapporterRegistreringStatusAntallForPublisering()
        val muligRegistreringTilstand = Optional.ofNullable(
            registreringTilstandRepository.finnNesteRegistreringTilstandMed(Status.OVERFORT_ARENA)
        )
        if (!muligRegistreringTilstand.isPresent) {
            LOG.info("Ingen registreringer klare (status = OVERFORT_ARENA) for publisering")
            return
        }
        val registreringTilstand = muligRegistreringTilstand.orElseThrow { IllegalStateException() }
        val brukerRegistreringId = registreringTilstand.brukerRegistreringId
        val bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId)
        val profilering = profileringRepository.hentProfileringForId(brukerRegistreringId)
        val ordinaerBrukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForId(brukerRegistreringId)

        // Det er viktig at publiserArbeidssokerRegistrert kjører før publiserProfilering fordi
        // førstnevnte sin producer håndterer at melding med samme id overskrives hvis den er publisert fra før.
        // Dette skjer pga. compaction-innstillingen definert i paw-iac repoet på github.
        // Så hvis førstnevnte feiler forhindrer vi at duplikate meldinger skrives til sistnevnte.
        val arbeidssokerRegistrertInternalEvent = ArbeidssokerRegistrertInternalEvent(
            bruker.aktorId,
            ordinaerBrukerRegistrering.besvarelse,
            ordinaerBrukerRegistrering.opprettetDato
        )

        val registrertOnprem =
            arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(arbeidssokerRegistrertInternalEvent)
        val registrertAiven =
            arbeidssokerRegistrertProducerAiven.publiserArbeidssokerRegistrert(arbeidssokerRegistrertInternalEvent)

        if (registrertOnprem && registrertAiven) {
            val oppdatertRegistreringTilstand = registreringTilstand.oppdaterStatus(Status.PUBLISERT_KAFKA)
            registreringTilstandRepository.oppdater(oppdatertRegistreringTilstand)
            LOG.info("Ny tilstand for registrering: {}", oppdatertRegistreringTilstand)
        }

        if (registrertOnprem) arbeidssokerProfilertProducer.publiserProfilering(
            bruker.aktorId,
            profilering.innsatsgruppe,
            ordinaerBrukerRegistrering.opprettetDato
        )

        if (registrertAiven) arbeidssokerProfilertProducerAiven.publiserProfilering(
            bruker.aktorId,
            profilering.innsatsgruppe,
            ordinaerBrukerRegistrering.opprettetDato
        )
    }

    /*@Transactional
    fun publiserEventsForFlereRegistreringer() {
        val nesteRegistreringTilstander = registreringTilstandRepository
            .finnFlereRegistreringTilstanderMed(1, Status.OVERFORT_ARENA)

        LOG.info("{} registreringer klare for publisering", nesteRegistreringTilstander.size)

        val nesteRegistreringer = brukerRegistreringRepository.hentBrukerregistreringerForIder(
            nesteRegistreringTilstander.map { it.brukerRegistreringId }
        )

        val nesteProfileringer = profileringRepository.hentProfileringerForIder(
            nesteRegistreringTilstander.map { it.brukerRegistreringId }
        )

        registreringTilstandRepository.oppdaterFlereTilstander(
            Status.PUBLISERT_KAFKA,
            nesteRegistreringTilstander.map { it.id })

        val resultatFraRegistrertProducer = nesteRegistreringer.map { (brukerRegistreringId, aktorOgReg) ->
            val (aktorId, ordinaerBrukerRegistrering) = aktorOgReg

            brukerRegistreringId to ArbeidssokerRegistrertInternalEvent(
                aktorId,
                ordinaerBrukerRegistrering.besvarelse,
                ordinaerBrukerRegistrering.opprettetDato
            )
        }.associate { (id, event) ->
            id to arbeidssokerRegistrertProducerAiven.publiserArbeidssokerRegistrert(event)
        }

        nesteProfileringer
            .filter { (id, _) -> resultatFraRegistrertProducer[id] ?: false }
            .forEach { (brukerRegistreringId, profilering) ->
                val (aktorId, ordinaerBrukerRegistrering) = nesteRegistreringer[brukerRegistreringId]
                    ?: throw IllegalStateException()

                arbeidssokerProfilertProducerAiven.publiserProfilering(
                    aktorId,
                    profilering.innsatsgruppe,
                    ordinaerBrukerRegistrering.opprettetDato
                )
            }
    }*/

    private fun rapporterRegistreringStatusAntallForPublisering() {
        try {
            val antallPerStatus = registreringTilstandRepository.hentAntallPerStatus()
            prometheusMetricsService.rapporterRegistreringStatusAntall(antallPerStatus)
        } catch (e: Exception) {
            LOG.error("Feil ved rapportering av antall statuser", e)
        }
    }

    fun harVentendeEvents(): Boolean {
        return registreringTilstandRepository.hentAntallPerStatus()[Status.OVERFORT_ARENA] != 0
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PubliseringAvEventsService::class.java)
    }
}