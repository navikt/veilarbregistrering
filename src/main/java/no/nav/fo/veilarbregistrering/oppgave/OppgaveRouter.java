package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
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
public class OppgaveRouter {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRouter.class);

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final EnhetGateway enhetGateway;
    private final Norg2Gateway norg2Gateway;
    private final PersonGateway personGateway;

    public OppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.enhetGateway = enhetGateway;
        this.norg2Gateway = norg2Gateway;
        this.personGateway = personGateway;
    }

    public Optional<Enhetsnr> hentEnhetsnummerFor(Bruker bruker, OppgaveType oppgaveType) {
        if (!oppgaveType.equals(OppgaveType.UTVANDRET)) {
            return Optional.empty();
        }

        Optional<GeografiskTilknytning> geografiskTilknytning;
        try {
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker.getFoedselsnummer());
        } catch (RuntimeException e) {
            LOG.warn("Henting av geografisk tilknytning feilet", e);
            geografiskTilknytning = Optional.empty();
        }

        if (geografiskTilknytning.isPresent()) {
            LOG.info("Fant geografisk tilknytning -> overlater til oppgave-api til å route selv");
            return Optional.empty();
        }

        try {
            return hentEnhetsnummerForSisteArbeidsforholdTil(bruker);
        } catch (RuntimeException e) {
            LOG.warn("Henting av enhetsnummer for siste arbeidsforhold feilet", e);
            return Optional.empty();
        }
    }

    public Optional<Enhetsnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getFoedselsnummer());
        if (!flereArbeidsforhold.sisteUtenNoeEkstra().isPresent()) {
            LOG.warn("Fant ingen arbeidsforhold knyttet til bruker");
            return Optional.empty();
        }
        Optional<Organisasjonsnummer> organisasjonsnummer = flereArbeidsforhold.sisteUtenNoeEkstra()
                .map(sisteArbeidsforhold -> sisteArbeidsforhold.getOrganisasjonsnummer())
                .orElseThrow(IllegalStateException::new);
        if (!organisasjonsnummer.isPresent()) {
            LOG.warn("Fant ingen organisasjonsnummer knyttet til det siste arbeidsforholdet");
            return Optional.empty();
        }

        Optional<Organisasjonsdetaljer> organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(organisasjonsnummer.get());
        if (!organisasjonsdetaljer.isPresent()) {
            LOG.warn("Fant ingen organisasjonsdetaljer knyttet til organisasjonsnummer: {}", organisasjonsnummer.get().asString());
            return Optional.empty();
        }

        Optional<Kommunenummer> kommunenummer = organisasjonsdetaljer
                .map(a -> a.kommunenummer())
                .orElseThrow(IllegalStateException::new);
        if (!kommunenummer.isPresent()) {
            LOG.warn("Fant ingen kommunenummer knyttet til organisasjon");
            return Optional.empty();
        }

        Optional<Enhetsnr> enhetsnr = norg2Gateway.hentEnhetFor(kommunenummer
                .orElseThrow(IllegalStateException::new));
        if (!enhetsnr.isPresent()) {
            LOG.warn("Fant ingen enhetsnummer knyttet til kommunenummer: {}", kommunenummer.get().asString());
        }
        return enhetsnr;
    }
}
