package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class OppgaveService {

    private final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private final OppgaveGateway oppgaveGateway;
    private final KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer;

    private final Map<OppgaveType, String> beskrivelser = new HashMap<>(1);

    public OppgaveService(OppgaveGateway oppgaveGateway, KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
        this.oppgaveGateway = oppgaveGateway;
        this.kontaktBrukerHenvendelseProducer = kontaktBrukerHenvendelseProducer;
        initBeskrivelser();
    }

    private void initBeskrivelser() {
        this.beskrivelser.put(
                OppgaveType.OPPHOLDSTILLATELSE,
                "Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, " +
                        "og har selv opprettet denne oppgaven. " +
                        "Ring bruker og følg midlertidig rutine på navet om løsning for registreringen av arbeids- og oppholdstillatelse."
        );
    }

    public Oppgave opprettOppgave(Bruker bruker, OppgaveType oppgaveType) {

        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId());

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                beskrivelser.get(oppgaveType));

        LOG.info("Oppgave ble opprettet med id: {} og ble tildelt enhet: {}", oppgave.getId(), oppgave.getTildeltEnhetsnr());

        return oppgave;
    }
}
