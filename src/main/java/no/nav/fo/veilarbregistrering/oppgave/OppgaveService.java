package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.apiapp.feil.Feil;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.Metrics;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final OppgaveRouter oppgaveRouter;
    private final KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer;
    private final Map<OppgaveType, String> beskrivelser;

    public OppgaveService(
            OppgaveGateway oppgaveGateway,
            OppgaveRepository oppgaveRepository,
            OppgaveRouter oppgaveRouter,
            KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {

        this.oppgaveGateway = oppgaveGateway;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveRouter = oppgaveRouter;
        this.kontaktBrukerHenvendelseProducer = kontaktBrukerHenvendelseProducer;
        beskrivelser = new HashMap<>(2);
        beskrivelser.put(
                OppgaveType.OPPHOLDSTILLATELSE,
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse."
        );
        beskrivelser.put(
                OppgaveType.UTVANDRET,
                "Brukeren får ikke registrert seg som arbeidssøker fordi bruker står som utvandret i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg vanlig rutine for slike tilfeller."

        );
    }

    public Oppgave opprettOppgave(Bruker bruker, OppgaveType oppgaveType) {
        validerNyOppgaveMotAktive(bruker, oppgaveType);

        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId(), oppgaveType);

        Optional<Enhetsnr> enhetsnr = oppgaveRouter.hentEnhetsnummerFor(bruker, oppgaveType);
        LocalDate fristFerdigstillelse = Virkedager.plussAntallArbeidsdager(idag(), 2);

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                enhetsnr.orElse(null),
                beskrivelser.get(oppgaveType),
                fristFerdigstillelse
        );

        LOG.info("Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}",
                oppgaveType, oppgave.getId(), oppgave.getTildeltEnhetsnr());

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
            LOG.info("Fant en oppgave av samme type som ble opprettet {} - {} timer siden.",
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
