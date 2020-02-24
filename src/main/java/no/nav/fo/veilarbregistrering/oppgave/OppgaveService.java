package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.OPPGAVE_OPPRETTET_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportTags;
import static no.nav.fo.veilarbregistrering.oppgave.NavKontor.*;

public class OppgaveService {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveService.class);

    private final OppgaveGateway oppgaveGateway;
    private final PersonGateway personGateway;
    private final KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer;

    private final Map<GeografiskTilknytning, NavKontor> navKontorMap = new HashMap<>(3);

    public OppgaveService(OppgaveGateway oppgaveGateway, PersonGateway personGateway, KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
        this.oppgaveGateway = oppgaveGateway;
        this.personGateway = personGateway;
        this.kontaktBrukerHenvendelseProducer = kontaktBrukerHenvendelseProducer;
        initNavKontor();
    }

    private void initNavKontor() {
        this.navKontorMap.put(GeografiskTilknytning.of("030102"), grünerlokka());
        this.navKontorMap.put(GeografiskTilknytning.of("3411"), ringsaker());
        this.navKontorMap.put(GeografiskTilknytning.of("500101"), falkenborg()); // Midtbyen - Trondheim
        this.navKontorMap.put(GeografiskTilknytning.of("500102"), falkenborg()); // Østbyen - Trondheim
    }

    public Oppgave opprettOppgave(Bruker bruker) {

        kontaktBrukerHenvendelseProducer.publiserHenvendelse(bruker.getAktorId());

        Optional<GeografiskTilknytning> muligGeografiskTilknytning = Optional.empty();
        try {
            muligGeografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker.getFoedselsnummer());
        } catch (RuntimeException e) {
            LOG.warn("Henting av geografisk tilknytning feilet ifm. opprettelse av oppgave. ", e);
        }

        GeografiskTilknytning geografiskTilknytning = muligGeografiskTilknytning.orElse(GeografiskTilknytning.of("030102"));
        NavKontor navKontor = navKontorMap.get(geografiskTilknytning);

        if (navKontor == null) {
            LOG.warn("{} er ikke mappet opp. Setter NAV Grünerløkka som default.", geografiskTilknytning);
            navKontor = NavKontor.grünerlokka();
        }

        Oppgave oppgave = oppgaveGateway.opprettOppgave(
                bruker.getAktorId(),
                navKontor.tilordnetRessurs(),
                navKontor.beskrivelse());

        reportTags(OPPGAVE_OPPRETTET_EVENT, geografiskTilknytning, TildeltEnhetsnr.of(oppgave.getTildeltEnhetsnr()));

        return oppgave;
    }
}
