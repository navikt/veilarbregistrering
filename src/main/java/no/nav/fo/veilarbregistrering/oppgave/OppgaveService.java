package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.*;

import java.util.*;

import static java.util.Collections.singletonList;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.report;

public class OppgaveService {

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

        report(OPPGAVE_OPPRETTET_EVENT, singletonList(TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr())), singletonList(oppgaveType));

        return oppgave;
    }
}
