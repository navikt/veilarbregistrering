package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PubliseringAvEventsService(
    private val profileringRepository: ProfileringRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val registrertProducer: ArbeidssokerRegistrertProducer,
    private val registrertProducerV2: ArbeidssokerRegistrertProducerV2,
    private val registreringTilstandRepository: RegistreringTilstandRepository,
    private val profilertProducer: ArbeidssokerProfilertProducer,
    private val metricsService: MetricsService
) {
    @Transactional
    fun publiserEvents() {
        rapporterRegistreringStatusAntallForPublisering()
        val registreringTilstand: RegistreringTilstand? =
            registreringTilstandRepository.finnNesteRegistreringTilstandMed(Status.OVERFORT_ARENA)

        LOG.info(
            "{} registrering klar (status = OVERFORT_ARENA) for publisering",
            registreringTilstand?.let { "1" } ?: "Ingen"
        )

        if (registreringTilstand == null) {
            return
        }
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

        val arbeidssokerRegistrertInternalEventV2 = ArbeidssokerRegistrertInternalEventV2(
            bruker.aktorId,
            ordinaerBrukerRegistrering.besvarelse,
            ordinaerBrukerRegistrering.opprettetDato
        )
        registrertProducerV2.publiserArbeidssokerRegistrert(arbeidssokerRegistrertInternalEventV2)
        if (registrertProducer.publiserArbeidssokerRegistrert(arbeidssokerRegistrertInternalEvent)) {
            val oppdatertRegistreringTilstand = registreringTilstand.oppdaterStatus(Status.PUBLISERT_KAFKA)
            registreringTilstandRepository.oppdater(oppdatertRegistreringTilstand)
            LOG.info("Ny tilstand for registrering: {}", oppdatertRegistreringTilstand)

            profilertProducer.publiserProfilering(
                bruker.aktorId,
                profilering.innsatsgruppe,
                ordinaerBrukerRegistrering.opprettetDato
            )
        }
    }

    private fun rapporterRegistreringStatusAntallForPublisering() {
        try {
            val antallPerStatus = registreringTilstandRepository.hentAntallPerStatus()
            metricsService.rapporterRegistreringStatusAntall(antallPerStatus)
        } catch (e: Exception) {
            LOG.error("Feil ved rapportering av antall statuser", e)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PubliseringAvEventsService::class.java)
    }
}
