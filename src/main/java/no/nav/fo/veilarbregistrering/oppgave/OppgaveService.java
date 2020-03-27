package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.report;

public class OppgaveService {

    private final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private final OppgaveGateway oppgaveGateway;
    private final KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer;

    public OppgaveService(OppgaveGateway oppgaveGateway, KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
        this.oppgaveGateway = oppgaveGateway;
        this.kontaktBrukerHenvendelseProducer = kontaktBrukerHenvendelseProducer;
    }

    public Oppgave opprettOppgave(Bruker bruker, OppgaveType oppgaveType) {

        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId());

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse.");

        LOG.info("Oppgave (type:{}) ble opprettet med id: {} og ble tildelt enhet: {}", oppgaveType, oppgave.getId(), oppgave.getTildeltEnhetsnr());

        try {
            report(OPPGAVE_OPPRETTET_EVENT, singletonList(TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr())), singletonList(oppgaveType));
        } catch (Exception e) {
            LOG.warn(String.format("Logging til influx feilet. Enhetsnr: {}, Oppgavetype: {}", TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr()).value(), oppgaveType.value()), e);
        }

        return oppgave;
    }
}
