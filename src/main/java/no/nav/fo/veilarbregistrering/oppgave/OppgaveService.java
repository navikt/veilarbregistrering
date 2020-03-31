package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportSimple;

public class OppgaveService {

    private final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

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
        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId());

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");

        LOG.info("Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}", oppgaveType, oppgave.getId(), oppgave.getTildeltEnhetsnr());

        if (skalLagreOppgave()) {
            LOG.info("Lagring av oppgave er togglet pa");
            oppgaveRepository.opprettOppgave(bruker.getAktorId(), oppgaveType, oppgave.getId());
        }

        try {
            reportSimple(OPPGAVE_OPPRETTET_EVENT, TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr()), oppgaveType);
        } catch (Exception e) {
            LOG.warn(String.format("Logging til influx feilet. Enhetsnr: %s, Oppgavetype: %s", oppgave.getTildeltEnhetsnr(), oppgaveType), e);
        }

        return oppgave;
    }

    private boolean skalLagreOppgave() {
        return unleashService.isEnabled("veilarbregistrering.lagreOppgave");
    }
}
