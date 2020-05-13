package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Hensikten med denne klassen, er å fjerne behovet for masse try/catch i OppgaveRouter,
 * samtidig som den skal håndtere de ulike Optional-verdiene. Ved å dele det i to, kan hver del bli enklere.
 */
public class OppgaveRouterProxy implements HentEnhetsIdForSisteArbeidsforhold {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRouterProxy.class);

    private final OppgaveRouter oppgaveRouter;

    public OppgaveRouterProxy(OppgaveRouter oppgaveRouter) {
        this.oppgaveRouter = oppgaveRouter;
    }

    @Override
    public Optional<Enhetsnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker) {
        try {
            return oppgaveRouter.hentEnhetsnummerForSisteArbeidsforholdTil(bruker);
        } catch (RuntimeException e) {
            LOG.warn("Henting av enhetsnummer for siste arbeidsforhold", e);
            return Optional.of(Enhetsnr.of(-1));
        }
    }
}
