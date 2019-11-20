package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;

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
            Metrics.report(OPPGAVE_OPPRETTET_EVENT, gt, TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr()));
        });

        return oppgave;
    }
}
