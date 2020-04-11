package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.apiapp.feil.Feil;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_ALLEREDE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportSimple;
import static no.nav.fo.veilarbregistrering.oppgave.OppgavePredicates.oppgaveAvTypeOppholdstillatelse;
import static no.nav.fo.veilarbregistrering.oppgave.OppgavePredicates.oppgaveOpprettetForMindreEnnToArbeidsdagerSiden;

public class OppgaveService {

    private final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private final OppgaveGateway oppgaveGateway;
    private final OppgaveRepository oppgaveRepository;
    private final KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer;

    public OppgaveService(
            OppgaveGateway oppgaveGateway,
            OppgaveRepository oppgaveRepository,
            KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {

        this.oppgaveGateway = oppgaveGateway;
        this.oppgaveRepository = oppgaveRepository;
        this.kontaktBrukerHenvendelseProducer = kontaktBrukerHenvendelseProducer;
    }

    public Oppgave opprettOppgave(Bruker bruker, OppgaveType oppgaveType) {
        validerNyOppgaveMotAktive(bruker, oppgaveType);

        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId());

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");

        LOG.info("Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}", oppgaveType, oppgave.getId(), oppgave.getTildeltEnhetsnr());

        oppgaveRepository.opprettOppgave(bruker.getAktorId(), oppgaveType, oppgave.getId());

        reportSimple(OPPGAVE_OPPRETTET_EVENT, TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr()), oppgaveType);

        return oppgave;
    }

    private void validerNyOppgaveMotAktive(Bruker bruker, OppgaveType oppgaveType) {
        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(bruker.getAktorId());
        Optional<OppgaveImpl> muligOppgave = oppgaver.stream()
                .filter(oppgaveAvTypeOppholdstillatelse())
                .filter(oppgaveOpprettetForMindreEnnToArbeidsdagerSiden(idag()))
                .findFirst();

        muligOppgave.ifPresent(oppgave -> {
            LOG.warn("Fant en oppgave av samme type som ble opprettet {} - {} timer siden.",
                    oppgave.getOpprettet().tidspunkt(),
                    oppgave.getOpprettet().antallTimerSiden());

            Metrics.reportSimple(OPPGAVE_ALLEREDE_OPPRETTET_EVENT, oppgave.getOpprettet(), oppgaveType);

            throw new Feil(new Feil.Type() {
                @Override
                public String getName() {
                    return "ALLEREDE_OPPRETTET_OPPGAVE";
                }

                @Override
                public Response.Status getStatus() {
                    return Response.Status.FORBIDDEN;
                }
            });
        });
    }

    /**
     * Protected metode for å kunne overskrive ifm. test.
     */
    protected LocalDate idag() {
        return LocalDate.now();
    }
}
