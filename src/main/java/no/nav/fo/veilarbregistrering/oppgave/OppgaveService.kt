package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import java.time.LocalDate

open class OppgaveService(
    private val oppgaveGateway: OppgaveGateway,
    private val oppgaveRepository: OppgaveRepository,
    private val oppgaveRouter: OppgaveRouter,
    private val prometheusMetricsService: PrometheusMetricsService
) {
    fun opprettOppgave(bruker: Bruker, oppgaveType: OppgaveType): OppgaveResponse {
        validerNyOppgaveMotAktive(bruker, oppgaveType)
        val enhetsnr = oppgaveRouter.hentEnhetsnummerFor(bruker)
        val oppgave = Oppgave.opprettOppgave(
            bruker.aktorId,
            enhetsnr.orElse(null),
            oppgaveType,
            idag()
        )
        val oppgaveResponse = oppgaveGateway.opprett(oppgave)
        LOG.info(
            "Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}",
            oppgaveType, oppgaveResponse.id, oppgaveResponse.tildeltEnhetsnr
        )
        oppgaveRepository.opprettOppgave(bruker.aktorId, oppgaveType, oppgaveResponse.id)
        prometheusMetricsService.registrer(Events.OPPGAVE_OPPRETTET_EVENT, oppgaveType)
        return oppgaveResponse
    }

    private fun validerNyOppgaveMotAktive(bruker: Bruker, oppgaveType: OppgaveType) {
        val oppgaver = oppgaveRepository.hentOppgaverFor(bruker.aktorId)
        val muligOppgave = oppgaver.stream()
            .filter(OppgavePredicates.oppgaveAvType(oppgaveType))
            .filter(OppgavePredicates.oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(idag()))
            .findFirst()
        muligOppgave.ifPresent { oppgave: OppgaveImpl ->
            prometheusMetricsService.registrer(Events.OPPGAVE_ALLEREDE_OPPRETTET_EVENT, oppgaveType)
            throw OppgaveAlleredeOpprettet(
                "Fant en oppgave av samme type ${oppgave.oppgavetype} " +
                        "som ble opprettet ${oppgave.opprettet.tidspunkt} - " +
                        "${oppgave.opprettet.antallTimerSiden()} timer siden."
            )
        }
    }

    /**
     * Protected metode for å kunne overskrive ifm. test.
     */
    protected open fun idag(): LocalDate {
        return LocalDate.now()
    }

    companion object {
        private val LOG = loggerFor<OppgaveService>()
    }
}