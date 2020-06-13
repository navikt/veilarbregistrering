package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class OppgaveForAvvistRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveForAvvistRegistreringService.class);

    private final OppgaveService oppgaveService;
    private final BrukerRegistreringRepository brukerRegistreringRepository;

    public OppgaveForAvvistRegistreringService(
            OppgaveService oppgaveService,
            BrukerRegistreringRepository brukerRegistreringRepository) {
        this.oppgaveService = oppgaveService;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
    }

    /**
     * Stegene som skal gjøres:
     * 1) Hente en registrering som har feilet
     * - avbryt hvis ingen funnet
     * 2) Hent grunnlaget for oppgaven;
     * - fødselsnummer (fra registreringen)
     * 3) Opprette oppgave
     * 4) Oppdatere status på registreringen
     */
    @Transactional
    public void opprettOppgaveAsynk() {
        Optional<AktiveringTilstand> muligRegistreringTilstand = brukerRegistreringRepository.finnNesteAktiveringTilstandSomHarFeilet();

        if (!muligRegistreringTilstand.isPresent()) {
            LOG.info("Fant ingen feilede registreringer (status = UTVANDRET, OPPHOLDSTILLATELSE) å opprette oppgave for");
            return;
        }

        AktiveringTilstand aktiveringTilstand = muligRegistreringTilstand.orElseThrow(IllegalStateException::new);
        long brukerRegistreringId = aktiveringTilstand.getBrukerRegistreringId();

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(brukerRegistreringId);

        oppgaveService.opprettOppgave(bruker, map(aktiveringTilstand.getStatus()));

        // TODO Trenger vi en kobling mellom oppgave og registrering i db?

    }

    private static OppgaveType map(Status status) {
        switch (status) {
            case DOD_UTVANDRET_ELLER_FORSVUNNET:
                return OppgaveType.UTVANDRET;
            case MANGLER_ARBEIDSTILLATELSE:
                return OppgaveType.OPPHOLDSTILLATELSE;
            default:
                throw new IllegalStateException(
                        String.format("Klarte ikke å mappe aktiveringsstatusen %s til OppgaveType", status));
        }
    }
}
