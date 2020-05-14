package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.NorgGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * <p>Har som hovedoppgave å route Oppgaver til riktig enhet.</p>
 *
 * <p>Oppgave-tjenesten router i utgangspunktet oppgaver selv, men for utvandrede brukere som
 * ikke ligger inne med Norsk adresse og tilknytning til NAV-kontor må vi gå via tidligere
 * arbeidsgiver.</p>
 */
public class OppgaveRouter implements HentEnhetsIdForSisteArbeidsforhold {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRouter.class);

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final EnhetGateway enhetGateway;
    private final NorgGateway norgGateway;

    public OppgaveRouter(ArbeidsforholdGateway arbeidsforholdGateway, EnhetGateway enhetGateway, NorgGateway norgGateway) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.enhetGateway = enhetGateway;
        this.norgGateway = norgGateway;
    }

    @Override
    public Optional<Enhetsnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getFoedselsnummer());
        if (!flereArbeidsforhold.sisteUtenNoeEkstra().isPresent()) {
            LOG.warn("Fant ingen arbeidsforhold knyttet til bruker");
            return Optional.empty();
        }
        Organisasjonsnummer organisasjonsnummer = flereArbeidsforhold.sisteUtenNoeEkstra()
                .map(sisteArbeidsforhold -> sisteArbeidsforhold.getOrganisasjonsnummer())
                .orElseThrow(IllegalStateException::new);

        Optional<Organisasjonsdetaljer> organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer);
        if (!organisasjonsdetaljer.isPresent()) {
            LOG.warn("Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer:{}", organisasjonsnummer.asString());
            return Optional.empty();
        }

        Optional<Kommunenummer> kommunenummer = organisasjonsdetaljer
                .map(a -> a.kommunenummer())
                .orElseThrow(IllegalStateException::new);
        if (!kommunenummer.isPresent()) {
            LOG.warn("Fant ingen kommunenummer knyttet til organisasjon");
            return Optional.empty();
        }

        Optional<Enhetsnr> enhetsnr = norgGateway.hentEnhetFor(kommunenummer
                .orElseThrow(IllegalStateException::new));
        if (!enhetsnr.isPresent()) {
            LOG.warn("Fant ingen enhetsnummer knyttet til kommunenummer:{}", kommunenummer.get().asString());
        }
        return enhetsnr;
    }
}
