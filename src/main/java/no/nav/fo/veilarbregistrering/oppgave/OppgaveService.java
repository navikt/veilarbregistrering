package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.apiapp.feil.Feil;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportSimple;

public class OppgaveService {

    private final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private static final int ANTALL_TIMER_GRENSE = 48;

    private final OppgaveGateway oppgaveGateway;
    private final OppgaveRepository oppgaveRepository;
    private final KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer;
    private final UnleashService unleashService;

    public OppgaveService(
            OppgaveGateway oppgaveGateway,
            OppgaveRepository oppgaveRepository,
            KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer,
            UnleashService unleashService) {

        this.oppgaveGateway = oppgaveGateway;
        this.oppgaveRepository = oppgaveRepository;
        this.kontaktBrukerHenvendelseProducer = kontaktBrukerHenvendelseProducer;
        this.unleashService = unleashService;
    }

    public Oppgave opprettOppgave(Bruker bruker, OppgaveType oppgaveType) {
        if (skalValidereNyOppgaveMotAktive()) {
            validerNyOppgaveMotAktive(bruker, oppgaveType);
        }
        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId());

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");

        LOG.info("Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}", oppgaveType, oppgave.getId(), oppgave.getTildeltEnhetsnr());

        oppgaveRepository.opprettOppgave(bruker.getAktorId(), oppgaveType, oppgave.getId());

        try {
            reportSimple(OPPGAVE_OPPRETTET_EVENT, TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr()), oppgaveType);
        } catch (Exception e) {
            LOG.warn(String.format("Logging til influx feilet. Enhetsnr: %s, Oppgavetype: %s", oppgave.getTildeltEnhetsnr(), oppgaveType), e);
        }

        return oppgave;
    }

    private void validerNyOppgaveMotAktive(Bruker bruker, OppgaveType oppgaveType) {
        List<OppgaveImpl> oppgaver = oppgaveRepository.hentOppgaverFor(bruker.getAktorId());
        Optional<OppgaveImpl> muligOppgave = oppgaver.stream()
                .filter(oppgave -> oppgave.getOppgavetype().equals(oppgaveType))
                .filter(oppgave -> oppgave.getOpprettet().isAfter(LocalDateTime.now().minusHours(ANTALL_TIMER_GRENSE)))
                .findFirst();

        muligOppgave.ifPresent(oppgave -> {
            LOG.warn("Fant en oppgave av samme type som ble opprettet {} - mindre enn {} timer siden", oppgave.getOpprettet(), ANTALL_TIMER_GRENSE);

            if (skalKasteEgenkomponertFeil()) {
                throw new Feil(new Feil.Type() {
                    @Override
                    public String getName() {
                        return "ALLEREDE_OPPRETTET_OPPGAVE";
                    }

                    @Override
                    public Response.Status getStatus() {
                        return Response.Status.TOO_MANY_REQUESTS;
                    }
                }, String.format("Du kan ikke opprette en ny oppgave før det har gått %s timer.", ANTALL_TIMER_GRENSE));
            } else {
                throw new ClientErrorException(String.format("Du kan ikke opprette en ny oppgave før det har gått %s time.", ANTALL_TIMER_GRENSE), Response.Status.TOO_MANY_REQUESTS);
            }
        });
    }

    private boolean skalValidereNyOppgaveMotAktive() {
        return unleashService.isEnabled("veilarbregistrering.validereOppgave");
    }

    private boolean skalKasteEgenkomponertFeil() {
        return unleashService.isEnabled("veilarbregistrering.validereOppgave.egenkomponertfeil");
    }
}
