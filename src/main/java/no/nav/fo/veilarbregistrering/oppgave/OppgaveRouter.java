package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.orgenhet.NorgGateway;

/**
 * <p>Har som hovedoppgave å route Oppgaver til riktig enhet.</p>
 *
 * <p>Oppgave-tjenesten router i utgangspunktet oppgaver selv, men for utvandrede brukere som
 * ikke ligger inne med Norsk adresse og tilknytning til NAV-kontor må vi gå via tidligere
 * arbeidsgiver.</p>
 */
public class OppgaveRouter {

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final EnhetGateway enhetGateway;
    private final NorgGateway norgGateway;

    public OppgaveRouter(ArbeidsforholdGateway arbeidsforholdGateway, EnhetGateway enhetGateway, NorgGateway norgGateway) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.enhetGateway = enhetGateway;
        this.norgGateway = norgGateway;
    }

    public TildeltEnhetsnr hentEnhetsnummerForSisteArbeidsgiver(Bruker bruker) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getFoedselsnummer());
        Organisasjonsnummer organisasjonsnummer = flereArbeidsforhold.siste().getOrganisasjonsnummer();

        Organisasjonsdetaljer organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer);
        Kommunenummer kommunenummer = organisasjonsdetaljer.kommunenummer();

        return norgGateway.hentEnhetFor(kommunenummer);
    }
}
