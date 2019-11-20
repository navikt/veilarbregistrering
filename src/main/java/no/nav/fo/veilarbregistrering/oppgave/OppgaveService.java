package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OppgaveService {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private final OppgaveGateway oppgaveGateway;
    private final PersonGateway personGateway;

    public OppgaveService(OppgaveGateway oppgaveGateway, PersonGateway personGateway) {
        this.oppgaveGateway = oppgaveGateway;
        this.personGateway = personGateway;
    }

    public Oppgave opprettOppgave(String aktorId, Foedselsnummer foedselsnummer) {
        Oppgave oppgave = oppgaveGateway.opprettOppgave(aktorId);

        Optional<GeografiskTilknytning> geografiskTilknytning = Optional.empty();
        try {
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(foedselsnummer);
        } catch (RuntimeException e) {
            LOG.warn("Henting av geografisk tilknytning feilet ifm. opprettelse av oppgave.", e);
        }

        geografiskTilknytning.ifPresent(gt -> {
              OppgaveMetrikker.rapporter(gt, oppgave.getTildeltEnhetsnr());
        });

        return oppgave;
    }
}
