package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDtoMapper.map
import java.time.LocalDate

class StartRegistreringStatusService(
    private val arbeidsforholdGateway: ArbeidsforholdGateway,
    private val brukerTilstandService: BrukerTilstandService,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val prometheusMetricsService: PrometheusMetricsService
) {
    fun hentStartRegistreringStatus(bruker: Bruker): StartRegistreringStatusDto {
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker)
        registrerFunksjonelleMetrikker(brukersTilstand)
        val muligGeografiskTilknytning = hentGeografiskTilknytning(bruker)

        muligGeografiskTilknytning.apply {
            logger.info(
                "Bruker {} startet registrering med geografisk tilknytning [BrukersTilstand], [GeografiskTilknytning] [{}] [{}]",
                bruker.aktorId,
                brukersTilstand,
                this
            )
        }

        val registreringType = brukersTilstand.registreringstype
        var oppfyllerBetingelseOmArbeidserfaring: Boolean? = null
        if (RegistreringType.ORDINAER_REGISTRERING == registreringType) {
            oppfyllerBetingelseOmArbeidserfaring =
                arbeidsforholdGateway.hentArbeidsforhold(bruker.gjeldendeFoedselsnummer)
                    .harJobbetSammenhengendeSeksAvTolvSisteManeder(LocalDate.now())
        }
        val startRegistreringStatus = map(
            brukersTilstand,
            muligGeografiskTilknytning,
            oppfyllerBetingelseOmArbeidserfaring,
            bruker.gjeldendeFoedselsnummer.alder(LocalDate.now())
        )
        logger.info("Startreg.status for {}: {}", bruker.aktorId, startRegistreringStatus)
        return startRegistreringStatus
    }

    fun registrerAtArenaHarPlanlagtNedetid() {
        prometheusMetricsService.registrer(Events.REGISTRERING_NEDETID_ARENA)
    }

    private fun hentGeografiskTilknytning(bruker: Bruker): GeografiskTilknytning? {
        val t1 = System.currentTimeMillis()

        val geografiskTilknytning = try {
            pdlOppslagGateway.hentGeografiskTilknytning(bruker.aktorId)?.also {
                logger.info("Henting av geografisk tilknytning tok {} ms.", System.currentTimeMillis() - t1)
            }
        } catch (e: RuntimeException) {
            logger.warn("Hent geografisk tilknytning fra PDL feilet. Skal ikke påvirke annen bruk.", e)
            null
        }
        return geografiskTilknytning
    }

    private fun registrerFunksjonelleMetrikker(brukersTilstand: BrukersTilstand) {
        prometheusMetricsService.registrer(Events.REGISTRERING_REGISTERINGSTYPE, brukersTilstand.registreringstype)
        brukersTilstand.servicegruppe?.let {
            prometheusMetricsService.registrer(Events.REGISTRERING_SERVICEGRUPPE, it)
        }
        brukersTilstand.rettighetsgruppe?.let {
            prometheusMetricsService.registrer(Events.REGISTRERING_RETTIGHETSGRUPPE, it)
        }

        if (brukersTilstand.registreringstype == RegistreringType.ALLEREDE_REGISTRERT &&
            brukersTilstand.formidlingsgruppe != null && brukersTilstand.servicegruppe != null
        ) {
            prometheusMetricsService.registrer(
                Events.REGISTRERING_ALLEREDEREGISTRERT,
                brukersTilstand.formidlingsgruppe,
                brukersTilstand.servicegruppe
            )
        }
    }
}