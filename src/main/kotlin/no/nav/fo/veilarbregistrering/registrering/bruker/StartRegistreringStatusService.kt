package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDtoMapper.map
import java.time.LocalDate

class StartRegistreringStatusService(
    private val arbeidsforholdGateway: ArbeidsforholdGateway,
    private val brukerTilstandService: BrukerTilstandService,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val metricsService: MetricsService
) {
    fun hentStartRegistreringStatus(bruker: Bruker): StartRegistreringStatusDto {
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker)
        registrerFunksjonelleMetrikker(brukersTilstand)
        val muligGeografiskTilknytning = hentGeografiskTilknytning(bruker)
        val registreringType = brukersTilstand.registreringstype

        var oppfyllerBetingelseOmArbeidserfaring: Boolean? = null
        if (RegistreringType.ORDINAER_REGISTRERING == registreringType) {
            oppfyllerBetingelseOmArbeidserfaring =
                arbeidsforholdGateway.hentArbeidsforhold(bruker.gjeldendeFoedselsnummer)
                    .harJobbetSammenhengendeSeksAvTolvSisteManeder(LocalDate.now())
        }

        logger.info("Brukers tilstand for {}: {}", bruker.aktorId, brukersTilstand)

        return map(
            brukersTilstand,
            muligGeografiskTilknytning,
            oppfyllerBetingelseOmArbeidserfaring,
            bruker.gjeldendeFoedselsnummer.alder(LocalDate.now())
        )
    }

    fun registrerAtArenaHarPlanlagtNedetid() {
        metricsService.registrer(Events.REGISTRERING_NEDETID_ARENA)
    }

    private fun hentGeografiskTilknytning(bruker: Bruker): GeografiskTilknytning? {
        val geografiskTilknytning = try {
            pdlOppslagGateway.hentGeografiskTilknytning(bruker.aktorId)
        } catch (e: RuntimeException) {
            logger.warn("Hent geografisk tilknytning fra PDL feilet. Skal ikke påvirke annen bruk.", e)
            null
        }
        return geografiskTilknytning
    }

    private fun registrerFunksjonelleMetrikker(brukersTilstand: BrukersTilstand) {
        metricsService.registrer(Events.REGISTRERING_REGISTERINGSTYPE, brukersTilstand.registreringstype)
        brukersTilstand.servicegruppe?.let {
            metricsService.registrer(Events.REGISTRERING_SERVICEGRUPPE, it)
        }
        brukersTilstand.rettighetsgruppe?.let {
            metricsService.registrer(Events.REGISTRERING_RETTIGHETSGRUPPE, it)
        }

        if (brukersTilstand.registreringstype == RegistreringType.ALLEREDE_REGISTRERT &&
            brukersTilstand.formidlingsgruppe != null && brukersTilstand.servicegruppe != null
        ) {
            metricsService.registrer(
                Events.REGISTRERING_ALLEREDEREGISTRERT,
                brukersTilstand.formidlingsgruppe,
                brukersTilstand.servicegruppe
            )
        }
    }
}