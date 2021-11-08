package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.metrics.Events.OPPGAVE_ALLEREDE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Events.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.oppgave.OppgavePredicates.oppgaveAvType;
import static no.nav.fo.veilarbregistrering.oppgave.OppgavePredicates.oppgaveOpprettetForMindreEnnToArbeidsdagerSiden;

public class OppgaveService {

    private final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private final OppgaveGateway oppgaveGateway;
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveRouter oppgaveRouter;
    private final PrometheusMetricsService prometheusMetricsService;

    public OppgaveService(
            OppgaveGateway oppgaveGateway,
            OppgaveRepository oppgaveRepository,
            OppgaveRouter oppgaveRouter,
            PrometheusMetricsService prometheusMetricsService) {

        this.oppgaveGateway = oppgaveGateway;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveRouter = oppgaveRouter;
        this.prometheusMetricsService = prometheusMetricsService;
    }

    public OppgaveResponse opprettOppgave(Bruker bruker, OppgaveType oppgaveType) {
        validerNyOppgaveMotAktive(bruker, oppgaveType);

        Optional<Enhetnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(bruker);

        Oppgave oppgave = Oppgave.opprettOppgave(bruker.getAktorId(),
                enhetsnr.orElse(null),
                oppgaveType,
                idag());

        OppgaveResponse oppgaveResponse = oppgaveGateway.opprett(oppgave);

        LOG.info("Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}",
                oppgaveType, oppgaveResponse.getId(), oppgaveResponse.getTildeltEnhetsnr());

        oppgaveRepository.opprettOppgave(bruker.getAktorId(), oppgaveType, oppgaveResponse.getId());

        prometheusMetricsService.registrer(OPPGAVE_OPPRETTET_EVENT, oppgaveType);

        return oppgaveResponse;
    }

    private void validerNyOppgaveMotAktive(Bruker bruker, OppgaveType oppgaveType) {
        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(bruker.getAktorId());
        Optional<OppgaveImpl> muligOppgave = oppgaver.stream()
                .filter(oppgaveAvType(oppgaveType))
                .filter(oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(idag()))
                .findFirst();

        muligOppgave.ifPresent(oppgave -> {
            prometheusMetricsService.registrer(OPPGAVE_ALLEREDE_OPPRETTET_EVENT, oppgaveType);

            throw new OppgaveAlleredeOpprettet(
                    String.format(
                            "Fant en oppgave av samme type %s som ble opprettet %s - %s timer siden.",
                            oppgave.getOppgavetype(),
                            oppgave.getOpprettet().tidspunkt(),
                            oppgave.getOpprettet().antallTimerSiden())
            );
        });
    }

    /**
     * Protected metode for å kunne overskrive ifm. test.
     */
    protected LocalDate idag() {
        return LocalDate.now();
    }

}
